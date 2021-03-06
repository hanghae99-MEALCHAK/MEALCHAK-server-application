package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.UserLocationUpdateDto;
import com.mealchak.mealchakserverapplication.dto.response.OtherUserInfoResponseDto;
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
    @DisplayName("?????? ?????? ??????")
    public void updateUserLocation() throws Exception {
        // given
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", "30???", "???", "??????", 50f, null);
        UserLocationUpdateDto updateDto = new UserLocationUpdateDto(123.123, 123.123, "????????? ?????????");

        // mocking
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        Location result = userService.updateUserLocation(updateDto, user);

        assertEquals(result.getLatitude(), user.getLocation().getLatitude());
        assertEquals(result.getLongitude(), user.getLocation().getLongitude());
        assertEquals(result.getAddress(), user.getLocation().getAddress());
        verify(userRepository, atLeastOnce()).findById(user.getId());
    }

    @Nested
    @DisplayName("?????? ?????? ?????? ?????? ?????????")
    class UpdateUserInfo_success {
        @Test
        @DisplayName("????????? ????????? ??????")
        public void updateUserInfo() throws Exception {
            User user = new User(100L, 101L, "?????????", "password", "test@test.com",
                    "https://gorokke.shop/image/profileDefaultImg.jpg", null, null, "??????", 50f, null);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            String userName = "Test_Update_User_Info";
            String comment = "Test_Success";
            String age = "20???";
            String gender = "??????";
            MultipartFile files = new MockMultipartFile("files", "test.jpeg", "image/jpeg", new FileInputStream("image/test.jpeg"));

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
        @DisplayName("????????? ???????????? ?????? ??????")
        public void updateUserInfo_1() throws Exception {
            User user = new User(100L, 101L, "?????????", "password", "test@test.com",
                    "https://gorokke.shop/image/profileDefaultImg.jpg", "30???", "??????", "??????", 50f, null);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            String userName = "Test_Update_User_Info";
            String comment = "Test_Success";
            String age = "20???";
            String gender = "??????";
            MultipartFile files = new MockMultipartFile("files", "test.jpeg", "image/jpeg", new FileInputStream("image/test.jpeg"));

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
    @DisplayName("?????? ?????? ?????? ?????? ?????????")
    class UpdateUserInfo_Fail {
        @Test
        @DisplayName("???????????? ?????? ??????")
        public void updateUserInfo_Fail_1() throws Exception {
            User user = new User(100L, 101L, null, "password", "test@test.com",
                    "https://gorokke.shop/image/profileDefaultImg.jpg", null, null, null, 50f, null);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            String userName = "Test_Update_User_Info";
            String comment = "Test_Success";
            String age = "20???";
            String gender = "??????";
            MultipartFile files = new MockMultipartFile("files", "test.rtf", "text/rtf", new FileInputStream("image/test.rtf"));

            // mocking
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            // when
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.updateUserInfo(files, null, null, userDetails, age, gender);
            });

            // then
            assertEquals("?????? ???????????? ?????????????????????.", exception.getMessage());
        }
        @Test
        @DisplayName("????????? ????????? ?????? ??????")
        public void updateUserInfo_Fail_2() throws Exception {
            User user = new User(100L, 101L, null, "password", "test@test.com",
                    "https://gorokke.shop/image/profileDefaultImg.jpg", null, null, null, 50f, null);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            String userName = "Test_Update_User_Info";
            String comment = "Test_Success";
            String age = "20???";
            String gender = "??????";
            MultipartFile files = new MockMultipartFile("files", "test.rtf", "text/rtf", new FileInputStream("image/test.rtf"));

            // when
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.updateUserInfo(files, null, null, userDetails, age, gender);
            });

            // then
            assertEquals("????????? ???????????? ????????????.", exception.getMessage());
        }
        @Test
        @DisplayName("????????? ?????? ?????? ??????")
        public void updateUserInfo_Fail_3() throws Exception {
            String userName = "Test_Update_User_Info";
            String comment = "Test_Success";
            String age = "20???";
            String gender = "??????";
            MultipartFile files = new MockMultipartFile("files", "test.rtf", "text/rtf", new FileInputStream("image/test.rtf"));

            // when
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.updateUserInfo(files,null,null,null,age,gender);
            });

            // then
            assertEquals("????????? ?????? ???????????????.", exception.getMessage());
        }
    }

    @Test
    @DisplayName("????????? pk????????? ?????? ??????")
    public void getUser() throws Exception {
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", null, null, "??????", 50f, null);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.getUser(user.getId());

        assertEquals(result, user);
        verify(userRepository, atLeastOnce()).findById(user.getId());
    }
    @Test
    @DisplayName("????????? pk????????? ?????? ?????? ??????")
    public void getUser_fail() throws Exception {
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", null, null, "??????", 50f, null);

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUser(user.getId());
        });

        // then
        assertEquals("????????? ????????????.", exception.getMessage());

    }

    @Test
    @DisplayName("??? ?????? ?????? ??????")
    public void getOtherUserInfo() throws Exception {
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", null, null, "??????", 50f, null);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(reviewRepository.findAllByUserIdOrderByCreatedAtDesc(any(),any())).thenReturn(any());

        OtherUserInfoResponseDto result = userService.getOtherUserInfo(user.getId());

        assertEquals(result.getAge(), user.getAge());
        assertEquals(result.getUserId(), user.getId());
        assertEquals(result.getGender(), user.getGender());
        assertEquals(result.getComment(), user.getComment());
        assertEquals(result.getUsername(), user.getUsername());
        assertEquals(result.getProfileImg(), user.getProfileImg());
        assertEquals(result.getMannerScore(), user.getMannerScore());

        verify(userRepository, atLeastOnce()).findById(user.getId());
        verify(reviewRepository, atLeastOnce()).findAllByUserIdOrderByCreatedAtDesc(user.getId(), ReviewListMapping.class);
    }

    @Test
    @DisplayName("??? ?????? ?????? ?????? ??????")
    public void getOtherUserInfo_fail() throws Exception {
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", null, null, "??????", 50f, null);

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getOtherUserInfo(user.getId());
        });

        // then
        assertEquals("userId ??? ???????????? ????????????.", exception.getMessage());
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    public void userInfo() {
    }

    @Test
    @DisplayName("????????? ?????????")
    public void kakaoLogin() {
    }

    @Test
    @DisplayName("?????????")
    public void login() {
    }

    @Test
    @DisplayName("?????? ??????")
    public void registerUser() {
    }
}