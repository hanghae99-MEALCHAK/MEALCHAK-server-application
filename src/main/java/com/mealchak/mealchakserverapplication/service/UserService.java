package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.SignupRequestDto;
import com.mealchak.mealchakserverapplication.dto.request.UserLocationUpdateDto;
import com.mealchak.mealchakserverapplication.dto.response.FileResponseDto;
import com.mealchak.mealchakserverapplication.dto.response.HeaderDto;
import com.mealchak.mealchakserverapplication.dto.response.UserInfoResponseDto;
import com.mealchak.mealchakserverapplication.jwt.JwtTokenProvider;
import com.mealchak.mealchakserverapplication.model.Location;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.KakaoOAuth2;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.oauth2.provider.KakaoUserInfo;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
import com.mealchak.mealchakserverapplication.repository.mapping.UserInfoMapping;
import com.mealchak.mealchakserverapplication.util.MD5Generator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final KakaoOAuth2 kakaoOAuth2;
    private final AuthenticationManager authenticationManager;
    private final FileService fileService;
    private static final String Pass_Salt = "AAABnv/xRVklrnYxKZ0aHgTBcXukeZygoC";

    public HeaderDto kakaoLogin(String authorizedCode) {
        // 카카오 OAuth2 를 통해 카카오 사용자 정보 조회
        KakaoUserInfo userInfo = kakaoOAuth2.getUserInfo(authorizedCode);
        Long kakaoId = userInfo.getId();
        String nickname = userInfo.getNickname();
        String email = userInfo.getEmail();
        String profileImg = userInfo.getProfileImg();
        String address = "서울 강남구 항해리99";
        double latitude = 37.497910;
        double longitutde = 127.027678;
        Location location = new Location(address, latitude, longitutde);

        // 우리 DB 에서 회원 Id 와 패스워드
        // 회원 Id = 카카오 nickname
        String username = nickname;
        // 패스워드 = 카카오 Id + ADMIN TOKEN
        String password = kakaoId + Pass_Salt;

        // DB 에 중복된 Kakao Id 가 있는지 확인
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);

        // 카카오 정보로 회원가입
        if (kakaoUser == null) {
            // 패스워드 인코딩
            String encodedPassword = passwordEncoder.encode(password);

            kakaoUser = new User(kakaoId, nickname, encodedPassword, email, profileImg, location);
            userRepository.save(kakaoUser);
        }

        // 로그인 처리
        Authentication kakaoUsernamePassword = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(kakaoUsernamePassword);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        HeaderDto headerDto = new HeaderDto();
        User member = userRepository.findByKakaoId(kakaoId).orElse(null);
        headerDto.setTOKEN(jwtTokenProvider.createToken(email, member.getId(), member.getUsername()));
        return headerDto;
    }

    // 테스트 회원가입
    @Transactional
    public void registerUser(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password;
        password = passwordEncoder.encode(requestDto.getPassword());
        User user = new User(username, password);
        userRepository.save(user);
    }

    // 유저 위치 저장
    @Transactional
    public Location updateUserLocation(UserLocationUpdateDto updateDto, User user) {
        User user1 = userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        Location location = new Location(updateDto);
        user1.updateUserDisc(location);
        return user1.getLocation();
    }

    //유저 정보 조회
    @Transactional
    public UserInfoMapping userInfo(UserDetailsImpl userDetails) {
        if (userDetails != null) {
            return userRepository.findByEmail(userDetails.getUser().getEmail(), UserInfoMapping.class).orElseThrow(() -> new IllegalArgumentException("회원이 아닙니다."));
        } else {
            throw new IllegalArgumentException("로그인 하지 않았습니다.");
        }
    }

    @Transactional
    public String login(SignupRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 유저입니다."));
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return jwtTokenProvider.createToken(user.getEmail(), user.getId(), user.getUsername());
    }

    // 유저 정보 수정
    @Transactional
    public UserInfoResponseDto updateUserInfo(MultipartFile files, String username, String comment, UserDetailsImpl userDetails) {

        if (userDetails != null) {
            User user = userRepository.findById(userDetails.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
            String filePath = user.getProfileImg();
            if (files != null) {
                try {
                    String originFilename = files.getOriginalFilename();
                    String nameToMD5 = new MD5Generator(originFilename).toString();
                    // 랜덤 키 생성
                    String uuid = UUID.randomUUID().toString();
                    // 랜덤 키와 파일명을 합쳐 파일명 중복을 피함
                    String filename = nameToMD5 + "_" + uuid;
                    // 해당 위치에 이미지 저장
                    String savePath = System.getProperty("user.dir") + "/image";
                    // 파일이 저장되는 폴더가 없으면 폴더를 생성
                    if (!new java.io.File(savePath).exists()) {
                        try {
                            new java.io.File(savePath).mkdir();
                        } catch (Exception e) {
                            System.out.println("디렉토리 생성 실패");
                        }
                    }
                    String fileType = files.getContentType();
                    filePath = savePath + "/" + filename;
                    files.transferTo(new java.io.File(filePath));

                    FileResponseDto responseDto = new FileResponseDto();
                    responseDto.setOriginFileName(originFilename);
                    responseDto.setFileName(filename);
                    responseDto.setFilePath(filePath);
                    responseDto.setFileType(fileType);

                    fileService.saveFile(responseDto);
                } catch (Exception e) {
                    System.out.println("파일 업로드 실패");
                }
            }
            if (username == null) {
                username = user.getUsername();
            }
            if (comment == null) {
                comment = user.getComment();
            }
            user.updateUserInfo(username, comment, filePath);
            return new UserInfoResponseDto(user);
        } else {
            throw new IllegalArgumentException("로그인 하지 않았습니다.");
        }
    }
}