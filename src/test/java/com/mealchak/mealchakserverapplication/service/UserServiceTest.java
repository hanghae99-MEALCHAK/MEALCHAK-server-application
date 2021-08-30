package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.UserLocationUpdateDto;
import com.mealchak.mealchakserverapplication.dto.response.UserInfoResponseDto;
import com.mealchak.mealchakserverapplication.jwt.JwtTokenProvider;
import com.mealchak.mealchakserverapplication.model.Location;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.KakaoOAuth2;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.ReviewRepository;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
import com.mealchak.mealchakserverapplication.repository.mapping.ReviewListMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private KakaoOAuth2 kakaoOAuth2;
    @Mock
    private AuthenticationManager authenticationManager;
    private static String Pass_Salt = "AAABnv/xRVklrnYxKZ0aHgTBcXukeZygoC";

    @Test
    @DisplayName("유저 위치 저장")
    public void updateUserLocation() throws Exception {
        // given
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", "30대", "남", "ㅎㅇ", 50f, null);
        UserLocationUpdateDto updateDto = new UserLocationUpdateDto(123.123, 123.123, "서울시 강남구");

        // mocking
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        Location result = userService.updateUserLocation(updateDto, user);

        assertEquals(result.getLatitude(), user.getLocation().getLatitude());
        assertEquals(result.getLongitude(), user.getLocation().getLongitude());
        assertEquals(result.getAddress(), user.getLocation().getAddress());
        verify(userRepository, atLeastOnce()).findById(user.getId());
    }

    @Test
    @DisplayName("유저 정보 조회")
    public void userInfo() {

    }

    @Nested
    @DisplayName("유저 정보 수정 성공 케이스")
    class UpdateUserInfo_success {
        @Test
        @DisplayName("이미지 첨부한 경우")
        public void updateUserInfo() throws Exception {
            User user = new User(100L, 101L, "테스트", "password", "test@test.com",
                    "/image/38c1e9b2e392e80eb083f05286687462_eb5ff497-c4d6-4258-b10e-2f2ccfb8a7bbIMG_3690.jpeg", null, null, "안녕", 50f, null);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            String userName = "Test_Update_User_Info";
            String comment = "Test_Success";
            String age = "20대";
            String gender = "남성";
            MultipartFile files = new MockMultipartFile("files", "IMG_3690.jpeg", "image/jpeg", new FileInputStream("image/IMG_3680.jpeg"));

            // mocking
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            // when
            UserInfoResponseDto result = userService.updateUserInfo(files, null, null, userDetails, age, gender);

            assertEquals(result.getAge(), user.getAge());
            assertEquals(result.getUsername(), user.getUsername());
            assertEquals(result.getComment(), user.getComment());
            assertEquals(result.getGender(), user.getGender());
            verify(userRepository, atLeastOnce()).findById(user.getId());
        }
        @Test
        @DisplayName("이미지 첨부하지 않은 경우")
        public void updateUserInfo_1() throws Exception {
            User user = new User(100L, 101L, "테스트", "password", "test@test.com",
                    "https://gorokke.shop/image/profileDefaultImg.jpg", "30대", "남성", "안녕", 50f, null);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            String userName = "Test_Update_User_Info";
            String comment = "Test_Success";
            String age = "20대";
            String gender = "남성";
            MultipartFile files = new MockMultipartFile("files", "IMG_3690.jpeg", "image/jpeg", new FileInputStream("image/IMG_3680.jpeg"));

            // mocking
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            // when
            UserInfoResponseDto result = userService.updateUserInfo(null, null, null, userDetails, null, null);

            assertEquals(result.getAge(), user.getAge());
            assertEquals(result.getUsername(), user.getUsername());
            assertEquals(result.getComment(), user.getComment());
            assertEquals(result.getGender(), user.getGender());
            verify(userRepository, atLeastOnce()).findById(user.getId());
        }
    }

    @Nested
    @DisplayName("유저 정보 수정 실패 케이스")
    class UpdateUserInfo_Fail {
        @Test
        @DisplayName("지원하지 않는 파일")
        public void updateUserInfo_Fail_1() throws Exception {
            User user = new User(100L, 101L, null, "password", "test@test.com",
                    "https://gorokke.shop/image/profileDefaultImg.jpg", null, null, null, 50f, null);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            String userName = "Test_Update_User_Info";
            String comment = "Test_Success";
            String age = "20대";
            String gender = "남성";
            MultipartFile files = new MockMultipartFile("files", "test.rtf", "text/rtf", new FileInputStream("image/test.rtf"));

            // mocking
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            // when
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.updateUserInfo(files, null, null, userDetails, age, gender);
            });

            // then
            assertEquals("파일 업로드에 실패하였습니다.", exception.getMessage());
        }
        @Test
        @DisplayName("저장된 유저가 없는 경우")
        public void updateUserInfo_Fail_2() throws Exception {
            User user = new User(100L, 101L, null, "password", "test@test.com",
                    "https://gorokke.shop/image/profileDefaultImg.jpg", null, null, null, 50f, null);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            String userName = "Test_Update_User_Info";
            String comment = "Test_Success";
            String age = "20대";
            String gender = "남성";
            MultipartFile files = new MockMultipartFile("files", "test.rtf", "text/rtf", new FileInputStream("image/test.rtf"));

            // when
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.updateUserInfo(files, null, null, userDetails, age, gender);
            });

            // then
            assertEquals("유저가 존재하지 않습니다.", exception.getMessage());
        }
        @Test
        @DisplayName("로그인 하지 않은 경우")
        public void updateUserInfo_Fail_3() throws Exception {
            String userName = "Test_Update_User_Info";
            String comment = "Test_Success";
            String age = "20대";
            String gender = "남성";
            MultipartFile files = new MockMultipartFile("files", "test.rtf", "text/rtf", new FileInputStream("image/test.rtf"));

            // when
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.updateUserInfo(files,null,null,null,age,gender);
            });

            // then
            assertEquals("로그인 하지 않았습니다.", exception.getMessage());
        }
    }

    @Test
    @DisplayName("유저의 pk값으로 유저 조회")
    public void getUser() throws Exception {
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", null, null, "ㅎㅇ", 50f, null);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.getUser(user.getId());

        assertEquals(result, user);
        verify(userRepository, atLeastOnce()).findById(user.getId());
    }
    @Test
    @DisplayName("유저의 pk값으로 유저 조회 실패")
    public void getUser_fail() throws Exception {
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", null, null, "ㅎㅇ", 50f, null);

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUser(user.getId());
        });

        // then
        assertEquals("회원이 아닙니다.", exception.getMessage());

    }

    @Test
    @DisplayName("타 유저 정보 조회")
    public void getOtherUserInfo() throws Exception {
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", null, null, "ㅎㅇ", 50f, null);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(reviewRepository.findAllByUserIdOrderByCreatedAtDesc(any(),any())).thenReturn(any());

        userService.getOtherUserInfo(user.getId());

        verify(userRepository, atLeastOnce()).findById(user.getId());
        verify(reviewRepository, atLeastOnce()).findAllByUserIdOrderByCreatedAtDesc(user.getId(), ReviewListMapping.class);
    }

    @Test
    @DisplayName("타 유저 정보 조회 실패")
    public void getOtherUserInfo_fail() throws Exception {
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", null, null, "ㅎㅇ", 50f, null);

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getOtherUserInfo(user.getId());
        });

        // then
        assertEquals("userId 가 존재하지 않습니다.", exception.getMessage());
    }
//    @Test
//    @DisplayName("로그인")
//    public void login() throws Exception {
//        User user = new User(100L, 101L, "user1", "123123123", "test@test.com",
//                "profileImg.jpg", null, null, "ㅎㅇ", 50f, null);
//        SignupRequestDto dto = new SignupRequestDto("user1", "123123123");
//
//        when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(user));
//
//        userService.login(dto);
//
//        verify(userRepository, atLeastOnce()).findByUsername(dto.getUsername());
//    }
}