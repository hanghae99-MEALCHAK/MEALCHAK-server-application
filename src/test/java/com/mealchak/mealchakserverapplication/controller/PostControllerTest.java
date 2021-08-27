package com.mealchak.mealchakserverapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealchak.mealchakserverapplication.MockSpringSecurityFilter;
import com.mealchak.mealchakserverapplication.config.WebSecurityConfig;
import com.mealchak.mealchakserverapplication.controller.PostController;
import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.PostResponseDto;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.service.ChatRoomService;
import com.mealchak.mealchakserverapplication.service.PostService;
import com.mealchak.mealchakserverapplication.service.UserRoomService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = PostController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
@MockBean(JpaMetamodelMappingContext.class)
public class PostControllerTest {

    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private ChatRoomService chatRoomService;

    @MockBean
    private UserRoomService userRoomService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();

        // Create mock principal for the test user
        User testUser = new User("test-username", "test-pwd");
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", Collections.emptyList());
    }

    @Test
    @DisplayName("포스트 생성")
    public void postCtrl1() throws Exception {
        PostRequestDto postRequestDto = new PostRequestDto("title", 4,
                "서울시 강남구", 37.24352, 126.87986, "restaurant",
                "2021 08 11 10 00", "contents", "분식");

        String postInfo = objectMapper.writeValueAsString(postRequestDto);

        mvc.perform(post("/posts")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("모집글 전체 불러오기")
    public void postCtrl2() throws Exception {
        List<PostResponseDto> dtoList = new ArrayList<>();
//        PostResponseDto postResponseDtoA = new PostResponseDto(100L, "titleA", "contentsA", 4, "분식", "A", "2021 08 08", "서울시 강남구", 101L, "user101", "image", 2.11, LocalDateTime.now(), 100L, 2L, true);
//        PostResponseDto postResponseDtoB = new PostResponseDto(200L, "titleB", "contentsB", 4, "한식", "B", "2021 08 08", "서울시 강남구", 201L, "user201", "image", 2.22, LocalDateTime.now(), 200L, 2L, true);
//        PostResponseDto postResponseDtoC = new PostResponseDto(300L, "titleC", "contentsC", 4, "중식", "C", "2021 08 08", "서울시 강남구", 301L, "user101", "image", 2.33, LocalDateTime.now(), 300L, 2L, true);
//
//        dtoList.add(postResponseDtoA);
//        dtoList.add(postResponseDtoB);
//        dtoList.add(postResponseDtoC);

        String category = "전체";
        when(postService.getAllPost(category)).thenReturn(dtoList);


        ResultActions resultActions = mvc.perform(get("/posts").param("category", "전체")
                .principal(mockPrincipal)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

    }
}
