package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.SignupRequestDto;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.KakaoOAuth2;
import com.mealchak.mealchakserverapplication.oauth2.provider.KakaoUserInfo;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final KakaoOAuth2 kakaoOAuth2;
    private final AuthenticationManager authenticationManager;
    private static final String Pass_Salt = "AAABnv/xRVklrnYxKZ0aHgTBcXukeZygoC";

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, KakaoOAuth2 kakaoOAuth2, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kakaoOAuth2 = kakaoOAuth2;
        this.authenticationManager = authenticationManager;
    }


    public String kakaoLogin(String authorizedCode) {
        // 카카오 OAuth2 를 통해 카카오 사용자 정보 조회
        KakaoUserInfo userInfo = kakaoOAuth2.getUserInfo(authorizedCode);
        Long kakaoId = userInfo.getId();
        String nickname = userInfo.getNickname();
        String email = userInfo.getEmail();

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

            kakaoUser = new User(kakaoId,nickname,encodedPassword,email);
            userRepository.save(kakaoUser);
        }

        // 로그인 처리
        Authentication kakaoUsernamePassword = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(kakaoUsernamePassword);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return email;
    }

    @Transactional
    public void registerUser(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password;


        Optional<User> found = userRepository.findByUsername(username);

        password = passwordEncoder.encode(requestDto.getPassword());
        User user = new User(username, password);
        userRepository.save(user);
    }

}