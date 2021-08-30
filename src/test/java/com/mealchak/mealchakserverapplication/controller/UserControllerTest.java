package com.mealchak.mealchakserverapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealchak.mealchakserverapplication.MockSpringSecurityFilter;
import com.mealchak.mealchakserverapplication.config.WebSecurityConfig;
import com.mealchak.mealchakserverapplication.dto.request.SignupRequestDto;
import com.mealchak.mealchakserverapplication.dto.request.UserLocationUpdateDto;
import com.mealchak.mealchakserverapplication.dto.response.UserInfoResponseDto;
import com.mealchak.mealchakserverapplication.jwt.JwtTokenProvider;
import com.mealchak.mealchakserverapplication.model.Location;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
import com.mealchak.mealchakserverapplication.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
@MockBean(JpaMetamodelMappingContext.class)
class UserControllerTest {

    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    private User testUser;
    private UserDetailsImpl testUserDetails;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();


        testUser = new User(102L, 103L, "user1", "password", "test@test.com",
                "profileImg.jpg", "30대", "남", "ㅎㅇ", 50f, null);
        testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", Collections.emptyList());
    }

    @Test
    @DisplayName("kakao소셜 로그인")
    public void kakaoLogin() throws Exception {
        mvc.perform(get("/user/kakao/callback")
                        .param("code", "code"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        verify(userService, atLeastOnce()).kakaoLogin("code");
    }

    @Test
    @DisplayName("유저 정보 조회")
    public void userInfo() throws Exception {
        mvc.perform(get("/user/info")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        verify(userService, atLeastOnce()).userInfo(testUserDetails);
    }

    @Test
    @DisplayName("유저 위치 저장")
    public void updateUserLocation() throws Exception {
        double latitude = 123.123;
        double longitude = 123.123;
        String address = "seoul";
        UserLocationUpdateDto updateDto = new UserLocationUpdateDto(latitude, longitude, address);

        Location location = new Location(updateDto);

        when(userService.updateUserLocation(any(), any())).thenReturn(location);


        mvc.perform(put("/user/location")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.latitude").value(location.getLatitude()))
                .andExpect(jsonPath("$.longitude").value(location.getLongitude()))
                .andExpect(jsonPath("$.address").value(location.getAddress()));
    }

    @Test
    @DisplayName("유저 정보 수정")
    public void updateUserInfo() throws Exception {
        String username = "test_update_userInfo";
        String comment = "test_update_success";
        String profileImg = "test_img";
        String age = "20대";
        String gender = "남성";
        UserInfoResponseDto responseDto = new UserInfoResponseDto(username, comment, profileImg, age, gender);


        when(userService.updateUserInfo(any(), any(), any(), any(), any(), any())).thenReturn(responseDto);

        mvc.perform(put("/userInfo/update")
                        .content(objectMapper.writeValueAsString(responseDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.username").value(responseDto.getUsername()))
                .andExpect(jsonPath("$.comment").value(responseDto.getComment()))
                .andExpect(jsonPath("$.profileImg").value(responseDto.getProfileImg()))
                .andExpect(jsonPath("$.age").value(responseDto.getAge()))
                .andExpect(jsonPath("$.gender").value(responseDto.getGender()));
    }

    @Test
    @DisplayName("타 유저 정보 조회")
    public void getOtherUserInfo() throws Exception {
        mvc.perform(get("/userInfo/{userId}", testUser.getId())
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        verify(userService, atLeastOnce()).getOtherUserInfo(testUser.getId());
    }

    @Test
    @DisplayName("로그인 요청")
    public void login() throws Exception {
        String username = "test";
        String password = "test_password";
        SignupRequestDto dto = new SignupRequestDto(username, password);
        mvc.perform(post("/user/login")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        verify(userService, atLeastOnce()).login(refEq(dto));
    }

    @Test
    @DisplayName("회원 가입 요청")
    public void registerUser() throws Exception {
        String username = "test";
        String password = "test_password";
        SignupRequestDto dto = new SignupRequestDto(username, password);
        mvc.perform(post("/user/signup")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        verify(userService, atLeastOnce()).registerUser(refEq(dto));
    }
}