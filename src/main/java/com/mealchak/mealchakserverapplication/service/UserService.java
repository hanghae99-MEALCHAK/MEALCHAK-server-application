package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.SignupRequestDto;
import com.mealchak.mealchakserverapplication.dto.request.UserLocationUpdateDto;
import com.mealchak.mealchakserverapplication.dto.response.HeaderDto;
import com.mealchak.mealchakserverapplication.dto.response.OtherUserInfoResponseDto;
import com.mealchak.mealchakserverapplication.dto.response.UserInfoMappingDto;
import com.mealchak.mealchakserverapplication.dto.response.UserInfoResponseDto;
import com.mealchak.mealchakserverapplication.jwt.JwtTokenProvider;
import com.mealchak.mealchakserverapplication.model.Location;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.KakaoOAuth2;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.oauth2.provider.KakaoUserInfo;
import com.mealchak.mealchakserverapplication.repository.JoinRequestQueryRepository;
import com.mealchak.mealchakserverapplication.repository.ReviewRepository;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
import com.mealchak.mealchakserverapplication.repository.mapping.ReviewListMapping;
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
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final ReviewRepository reviewRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final KakaoOAuth2 kakaoOAuth2;
    private final AuthenticationManager authenticationManager;
    private final ChatRoomService chatRoomService;
    private final JoinRequestQueryRepository joinRequestQueryRepository;
    private static final String Pass_Salt = "AAABnv/xRVklrnYxKZ0aHgTBcXukeZygoC";

    // ????????? ?????????
    public HeaderDto kakaoLogin(String authorizedCode) {
        // ????????? OAuth2 ??? ?????? ????????? ????????? ?????? ??????
        KakaoUserInfo userInfo = kakaoOAuth2.getUserInfo(authorizedCode);
        Long kakaoId = userInfo.getId();
        String email = userInfo.getEmail();
        String address = "????????? ???????????? ????????? ???????????????!";
        double latitude = 37.497910;
        double longitude = 127.027678;
        Location location = new Location(address, latitude, longitude);

        // ???????????? = ????????? Id + ADMIN TOKEN
        String password = kakaoId + Pass_Salt;

        // DB ??? ????????? Kakao Id ??? ????????? ??????
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);

        // ????????? ????????? ????????????
        if (kakaoUser == null) {
            // ???????????? ?????????
            String encodedPassword = passwordEncoder.encode(password);

            kakaoUser = User.builder()
                    .kakaoId(kakaoId)
                    .email(email)
                    .password(encodedPassword)
                    .username(userInfo.getNickname())
                    .profileImg(userInfo.getProfileImg())
                    .age(userInfo.getAge())
                    .gender(userInfo.getGender())
                    .location(location)
                    .mannerScore(5.0f)
                    .build();
            userRepository.save(kakaoUser);
        }

        // ????????? ??????
        Authentication kakaoUsernamePassword = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(kakaoUsernamePassword);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        HeaderDto headerDto = new HeaderDto();

        // ????????? ?????? ??? ?????? ?????? ????????? ???????????? JWT????????? ???????????? ?????? ????????? Dto??? ????????? ??????
        User member = userRepository.findByKakaoId(kakaoId).orElseThrow(()
                -> new IllegalArgumentException("???????????? ?????? ???????????????."));
        headerDto.setTOKEN(jwtTokenProvider.createToken(email, member.getId(), member.getUsername()));
        return headerDto;
    }

    // ????????? ????????????
    @Transactional
    public void registerUser(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password;
        password = passwordEncoder.encode(requestDto.getPassword());
        User user = new User(username, password);
        userRepository.save(user);
    }

    // ?????? ?????? ??????
    @Transactional
    public Location updateUserLocation(UserLocationUpdateDto updateDto, User user) {
        User user1 = userRepository.findById(user.getId()).orElseThrow(()
                -> new IllegalArgumentException("?????? ????????? ???????????? ????????????."));
        Location location = new Location(updateDto);
        user1.updateUserDisc(location);
        return user1.getLocation();
    }

    //?????? ?????? ??????
    @Transactional
    public UserInfoMappingDto userInfo(UserDetailsImpl userDetails) {
        if (userDetails != null) {
            UserInfoMapping userInfoMapping = userRepository.findByEmail(userDetails.getUser().getEmail(), UserInfoMapping.class).orElseThrow(()
                    -> new IllegalArgumentException("????????? ????????????."));
            boolean newJoinRequest = joinRequestQueryRepository.existByUserId(userInfoMapping.getId());
            boolean newMessage = chatRoomService.newMessage(userDetails);
            return new UserInfoMappingDto(userInfoMapping, newMessage, newJoinRequest);
        } else {
            throw new IllegalArgumentException("????????? ?????? ???????????????.");
        }
    }

    // ????????? ?????????
    @Transactional
    public String login(SignupRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("???????????? ?????? ???????????????."));
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("????????? ?????????????????????.");
        }
        return jwtTokenProvider.createToken(user.getEmail(), user.getId(), user.getUsername());
    }

    // ?????? ?????? ??????
    @Transactional
    public UserInfoResponseDto updateUserInfo(MultipartFile files, String username, String comment, UserDetailsImpl userDetails, String age, String gender) {
        if (userDetails != null) {
            User user = userRepository.findById(userDetails.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("????????? ???????????? ????????????."));
            String filename;
            if (files != null) {
                try {
                    String originFilename = Objects.requireNonNull(files.getOriginalFilename()).replaceAll(" ", "");
                    String formatName = originFilename.substring(originFilename.lastIndexOf(".") + 1).toLowerCase();
                    String[] supportFormat = {"bmp", "jpg", "jpeg", "png"};
                    if (!Arrays.asList(supportFormat).contains(formatName)) {
                        throw new IllegalArgumentException("???????????? ?????? format ?????????.");
                    }
                    String nameToMD5 = new MD5Generator(originFilename).toString();
                    // ?????? ??? ??????
                    String uuid = UUID.randomUUID().toString();
                    // ?????? ?????? ???????????? ?????? ????????? ????????? ??????
                    filename = nameToMD5 + "_" + uuid + originFilename;
                    // ?????? ????????? ????????? ??????
                    String savePath = System.getProperty("user.dir") + "/image";
                    // ????????? ???????????? ????????? ????????? ????????? ??????
                    if (!new java.io.File(savePath).exists()) {
                        new java.io.File(savePath).mkdir();
                    }
                    String defaultImg = "https://gorokke.shop/image/profileDefaultImg.jpg"; // AWS EC2
//                    String defaultImg = "http://115.85.182.57/image/profileDefaultImg.jpg"; // NAVER EC2
                    if (!user.getProfileImg().contains("k.kakaocdn.net/dn/") && !user.getProfileImg().contains(defaultImg)) {
                        String[] deleteImg = userDetails.getUser().getProfileImg().split("/image");
                        File deleteFile = new File(System.getProperty("user.dir") + "/image" + deleteImg[1]);
                        if (deleteFile.exists()) {
                            deleteFile.delete();
                        }
                    }
                    // ????????? ??????
                    String filePath = savePath + "/" + filename;
                    File newFile = new File(filePath);
                    files.transferTo(new java.io.File(filePath));
//                    filename = "http://115.85.182.57/image/" + filename;  // NAVER EC2
                    filename = "https://gorokke.shop/image/" + filename;   // AWS EC2
                } catch (Exception e) {
                    throw new IllegalArgumentException("?????? ???????????? ?????????????????????.");
                }
            } else {
                filename = user.getProfileImg();
            }

            if (username == null) {
                username = user.getUsername();
            }
            if (comment == null) {
                comment = user.getComment();
            }
            if (user.getAge() == null) {
                user.updateAge(age);
            }
            if (user.getGender() == null) {
                user.updateGender(gender);
            }

            user.updateUserInfo(username, comment, filename);
            return new UserInfoResponseDto(user);
        } else {
            throw new IllegalArgumentException("????????? ?????? ???????????????.");
        }
    }

    // ????????? pk ????????? ?????? ??????
    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("????????? ????????????."));
    }

    // ??? ?????? ?????? ??????
    public OtherUserInfoResponseDto getOtherUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("userId ??? ???????????? ????????????."));
        List<ReviewListMapping> reviews = reviewRepository.findAllByUserIdOrderByCreatedAtDesc(userId, ReviewListMapping.class);
        return new OtherUserInfoResponseDto(user, reviews);
    }
}