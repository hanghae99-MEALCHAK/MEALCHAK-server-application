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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void updateUserLocation() {
        // given
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", "30대", "남" ,"ㅎㅇ", 50f, null);
        UserLocationUpdateDto updateDto = new UserLocationUpdateDto(123.123, 123.123, "서울시 강남구");

        // mocking
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        Location result = userService.updateUserLocation(updateDto, user);

        assertEquals(result.getLatitude(), user.getLocation().getLatitude());
        assertEquals(result.getLongitude(), user.getLocation().getLongitude());
        assertEquals(result.getAddress(), user.getLocation().getAddress());
        verify(userRepository).findById(user.getId());
    }

    @Test
    @DisplayName("유저 정보 조회")
    public void userInfo() {

    }

    @Test
    @DisplayName("유저 정보 수정")
    public void updateUserInfo() {
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", null, null ,"ㅎㅇ", 50f, null);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String userName = "Test_Update_User_Info";
        String comment = "Test_Success";
        String age = "20대";
        String gender = "남성";

        // mocking
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        UserInfoResponseDto result = userService.updateUserInfo(null, userName, comment, userDetails, age, gender);

        assertEquals(result.getAge(), user.getAge());
        assertEquals(result.getUsername(), user.getUsername());
        assertEquals(result.getComment(), user.getComment());
        assertEquals(result.getGender(), user.getGender());
        verify(userRepository).findById(user.getId());
    }

    @Test
    @DisplayName("유저 pk값으로 유저 조회")
    public void getUser() {
        User user = new User(100L, 101L, "user1", "password", "test@test.com",
                "profileImg.jpg", null, null ,"ㅎㅇ", 50f, null);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.getUser(user.getId());

        assertEquals(result, user);
        verify(userRepository).findById(user.getId());
    }

    @Test
    @DisplayName("타 유저 정보 조회")
    public void getOtherUserInfo() {
    }

}