package com.mealchak.mealchakserverapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealchak.mealchakserverapplication.MockSpringSecurityFilter;
import com.mealchak.mealchakserverapplication.config.WebSecurityConfig;
import com.mealchak.mealchakserverapplication.dto.response.MyAwaitRequestJoinResponseDto;
import com.mealchak.mealchakserverapplication.dto.response.PostResponseDto;
import com.mealchak.mealchakserverapplication.dto.response.UserInfoAndPostResponseDto;
import com.mealchak.mealchakserverapplication.model.*;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.service.JoinRequestsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = JoinRequestController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
@MockBean(JpaMetamodelMappingContext.class)
class JoinRequestControllerTest {

    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private JoinRequestsService joinRequestsService;

    private Post post;
    private UserDetailsImpl testUserDetails;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();

        // Create mock principal for the test user
        User testUser = new User("test-username", "test-pwd");
        testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", Collections.emptyList());

        Location location = new Location("서울특별시 강남구", 37.111111, 126.111111);
        Menu menu = new Menu("카페", 1);
        ChatRoom chatRoom = new ChatRoom("UUID111", testUserDetails.getUser());
        post = new Post(100L, "title", 3, "restaurant01", "2021-09-01 00:00:00",
                "contents", true, false, chatRoom, testUserDetails.getUser(), menu, location,
                2.00, 1L);
    }

    @Test
    @DisplayName("입장신청")
    void requestJoin() throws Exception {

        when(joinRequestsService.requestJoin(any(), anyLong())).thenReturn("신청완료");

        mvc.perform(get("/posts/join/request/{postId}" ,post.getId())
                .principal(mockPrincipal)
        )
                .andExpect(status().isOk())
                .andExpect(content().string("신청완료"))
                .andDo(MockMvcResultHandlers.print());

        verify(joinRequestsService, times(1)).requestJoin(any(), anyLong());
    }

    @Test
    @DisplayName("게시글 입장 신청 취소")
    void requestJoinCancel() throws Exception {
        mvc.perform(delete("/posts/join/request/{id}", 100L)
                .principal(mockPrincipal)
        )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        verify(joinRequestsService, times(1)).requestJoinCancel(any(), anyLong());
    }

    @Test
    @DisplayName("게시글 입장 신청 목록")
    void requestJoinList() throws Exception {
        UserInfoAndPostResponseDto userInfoAndPostResponseDto
                = new UserInfoAndPostResponseDto(100L, 100L,
                testUserDetails.getUsername(),"https://gorokke.shop/image/profileDefaultImg.jpg", "title");
        List<UserInfoAndPostResponseDto> userInfoAndPostResponseDtoList = new ArrayList<>();
        userInfoAndPostResponseDtoList.add(userInfoAndPostResponseDto);

        when(joinRequestsService.requestJoinList(testUserDetails))
                .thenReturn(userInfoAndPostResponseDtoList);

        mvc.perform(get("/posts/join/request/list")
                .principal(mockPrincipal)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].joinRequestId").value(userInfoAndPostResponseDto.getJoinRequestId()))
                .andExpect(jsonPath("$.[0].userId").value(100L))
                .andExpect(jsonPath("$.[0].username").value(testUserDetails.getUsername()))
                .andExpect(jsonPath("$.[0].profileImg").value(userInfoAndPostResponseDto.getProfileImg()))
                .andExpect(jsonPath("$.[0].postTitle").value(userInfoAndPostResponseDto.getPostTitle()))
                .andDo(MockMvcResultHandlers.print());

        verify(joinRequestsService, times(1)).requestJoinList(testUserDetails);

    }

    @Test
    @DisplayName("게시글 입장 신청 승인/비승인")
    void acceptJoinRequest() throws Exception {

        when(joinRequestsService.acceptJoinRequest(100L, true)).thenReturn("승인되었습니다");
        mvc.perform(get("/posts/join/request/accept/{joinRequestId}", 100L)
                .param("accept", "true")
                .principal(mockPrincipal)
        )
                .andExpect(status().isOk())
                .andExpect(content().string("승인되었습니다"))
                .andDo(MockMvcResultHandlers.print());

        verify(joinRequestsService, times(1)).acceptJoinRequest(100L, true);

    }

    @Test
    @DisplayName("나의 게시글 입장 신청 대기 목록")
    void myAwaitRequestJoinList() throws Exception {
        MyAwaitRequestJoinResponseDto myAwaitRequestJoinResponseDto
                = new MyAwaitRequestJoinResponseDto(100L, "postTitle");
        List<MyAwaitRequestJoinResponseDto> myAwaitRequestJoinResponseDtoList = new ArrayList<>();
        myAwaitRequestJoinResponseDtoList.add(myAwaitRequestJoinResponseDto);

        when(joinRequestsService.myAwaitRequestJoinList(testUserDetails)).thenReturn(myAwaitRequestJoinResponseDtoList);

        mvc.perform(get("/posts/join/request/await")
                .principal(mockPrincipal)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].joinRequestId").value(100L))
                .andExpect(jsonPath("$.[0].postTitle").value("postTitle"))
                .andDo(MockMvcResultHandlers.print());

        verify(joinRequestsService, times(1)).myAwaitRequestJoinList(testUserDetails);
    }
}