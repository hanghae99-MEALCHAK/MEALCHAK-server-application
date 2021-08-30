package com.mealchak.mealchakserverapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealchak.mealchakserverapplication.MockSpringSecurityFilter;
import com.mealchak.mealchakserverapplication.config.WebSecurityConfig;
import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.PostDetailResponseDto;
import com.mealchak.mealchakserverapplication.dto.response.PostResponseDto;
import com.mealchak.mealchakserverapplication.model.*;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private PostService postService;

    @MockBean
    private ChatRoomService chatRoomService;

    @MockBean
    private UserRoomService userRoomService;

    private UserDetailsImpl testUserDetails;
    private Post post;
    private List<PostResponseDto> postResponseDtoList;
    private PostResponseDto postResponseDto;

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

        postResponseDtoList = new ArrayList<>();
        postResponseDto = new PostResponseDto(post);
        postResponseDtoList.add(postResponseDto);
    }

    @Test
    @DisplayName("포스트 생성")
    public void createPost() throws Exception {
        PostRequestDto postRequestDto = new PostRequestDto("title", 4,
                "서울시 강남구", 37.24352, 126.87986, "restaurant",
                "2021 08 11 10 00", "contents", "분식");

        String postInfo = objectMapper.writeValueAsString(postRequestDto);

        mvc.perform(post("/posts")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("모집글 전체 불러오기")
    public void getAllPost() throws Exception {

        when(postService.getAllPost("전체")).thenReturn(postResponseDtoList);

        mvc.perform(get("/posts")
                        .param("category", "전체")
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.[0].postId").value(postResponseDto.getPostId()))
                .andExpect(jsonPath("$.[0].title").value(postResponseDto.getTitle()))
                .andExpect(jsonPath("$.[0].headCount").value(postResponseDto.getHeadCount()))
                .andExpect(jsonPath("$.[0].restaurant").value(postResponseDto.getRestaurant()))
                .andExpect(jsonPath("$.[0].orderTime").value(postResponseDto.getOrderTime()))
                .andExpect(jsonPath("$.[0].contents").value(postResponseDto.getContents()))
                .andExpect(jsonPath("$.[0].category").value(postResponseDto.getCategory()))
                .andExpect(jsonPath("$.[0].address").value(postResponseDto.getAddress()))
                .andExpect(jsonPath("$.[0].distance").value(postResponseDto.getDistance()))
                .andExpect(jsonPath("$.[0].userId").value(postResponseDto.getUserId()))
                .andExpect(jsonPath("$.[0].username").value(postResponseDto.getUsername()))
                .andExpect(jsonPath("$.[0].profileImg").value(postResponseDto.getProfileImg()))
                .andExpect(jsonPath("$.[0].createdAt").value(postResponseDto.getCreatedAt()))
                .andExpect(jsonPath("$.[0].roomId").value(postResponseDto.getRoomId()))
                .andExpect(jsonPath("$.[0].nowHeadCount").value(postResponseDto.getNowHeadCount()))
                .andExpect(jsonPath("$.[0].valid").value(postResponseDto.getValid()))
                .andDo(MockMvcResultHandlers.print());

        verify(postService, times(1)).getAllPost("전체");
    }

    @Test
    @DisplayName("해당_모집글_불러오기")
    void getPostDetail() throws Exception {
        List<User> userList = new ArrayList<>();
        PostDetailResponseDto postDetailResponseDto = new PostDetailResponseDto(post, userList);

        when(postService.getPostDetail(post.getId())).thenReturn(postDetailResponseDto);

        mvc.perform(get("/posts/{postId}", post.getId())
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.postId").value(post.getId()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.headCount").value(post.getHeadCount()))
                .andExpect(jsonPath("$.restaurant").value(post.getRestaurant()))
                .andExpect(jsonPath("$.orderTime").value(post.getOrderTime()))
                .andExpect(jsonPath("$.contents").value(post.getContents()))
                .andExpect(jsonPath("$.category").value(post.getMenu().getCategory()))
                .andExpect(jsonPath("$.address").value(post.getLocation().getAddress()))
                .andExpect(jsonPath("$.latitude").value(post.getLocation().getLatitude()))
                .andExpect(jsonPath("$.longitude").value(post.getLocation().getLongitude()))
                .andExpect(jsonPath("$.distance").value(post.getDistance()))
                .andExpect(jsonPath("$.userId").value(post.getUser().getId()))
                .andExpect(jsonPath("$.username").value(post.getUser().getUsername()))
                .andExpect(jsonPath("$.profileImg").value(post.getUser().getProfileImg()))
                .andExpect(jsonPath("$.createdAt").value(post.getCreatedAt()))
                .andExpect(jsonPath("$.roomId").value(post.getChatRoom().getId()))
                .andExpect(jsonPath("$.nowHeadCount").value(post.getNowHeadCount()))
                .andExpect(jsonPath("$.userList").value(userList))
                .andDo(MockMvcResultHandlers.print());
        verify(postService, times(1)).getPostDetail(post.getId());
    }

    @Test
    @DisplayName("내가 쓴 모집글 불러오기")
    void getMyPost() throws Exception {

        when(postService.getMyPost(testUserDetails)).thenReturn(postResponseDtoList);

        mvc.perform(get("/posts/myPosts")
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].postId").value(postResponseDto.getPostId()))
                .andExpect(jsonPath("$.[0].title").value(postResponseDto.getTitle()))
                .andExpect(jsonPath("$.[0].headCount").value(postResponseDto.getHeadCount()))
                .andExpect(jsonPath("$.[0].restaurant").value(postResponseDto.getRestaurant()))
                .andExpect(jsonPath("$.[0].orderTime").value(postResponseDto.getOrderTime()))
                .andExpect(jsonPath("$.[0].contents").value(postResponseDto.getContents()))
                .andExpect(jsonPath("$.[0].category").value(postResponseDto.getCategory()))
                .andExpect(jsonPath("$.[0].address").value(postResponseDto.getAddress()))
                .andExpect(jsonPath("$.[0].distance").value(postResponseDto.getDistance()))
                .andExpect(jsonPath("$.[0].userId").value(postResponseDto.getUserId()))
                .andExpect(jsonPath("$.[0].username").value(postResponseDto.getUsername()))
                .andExpect(jsonPath("$.[0].profileImg").value(postResponseDto.getProfileImg()))
                .andExpect(jsonPath("$.[0].createdAt").value(postResponseDto.getCreatedAt()))
                .andExpect(jsonPath("$.[0].roomId").value(postResponseDto.getRoomId()))
                .andExpect(jsonPath("$.[0].nowHeadCount").value(postResponseDto.getNowHeadCount()))
                .andExpect(jsonPath("$.[0].valid").value(postResponseDto.getValid()))
                .andDo(MockMvcResultHandlers.print());

        verify(postService, times(1)).getMyPost(testUserDetails);
    }

    @Test
    @DisplayName("해당 모집글 수정")
    void updatePostDetail() throws Exception {
        PostRequestDto postRequestDto = new PostRequestDto("title", 4,
                "서울시 강남구", 37.24352, 126.87986, "restaurant",
                "2021 08 11 10 00", "contents", "분식");

        when(postService.updatePostDetail(refEq(post.getId()), refEq(postRequestDto), refEq(testUserDetails)))
                .thenReturn(postResponseDto);

        String postInfo = objectMapper.writeValueAsString(postRequestDto);

        mvc.perform(put("/posts/{postId}", post.getId())
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        verify(postService, times(1))
                .updatePostDetail(refEq(post.getId()), refEq(postRequestDto), refEq(testUserDetails));

    }

    @Test
    @DisplayName("특정 모집글 삭제")
    void getPostDelete() throws Exception {

        mvc.perform(delete("/posts/{postId}", post.getId())
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        verify(postService, times(1)).deletePost(anyLong(), any());
    }

    @Test
    @DisplayName("검색하여 모집글 불러오기")
    void getSearch() throws Exception {

        when(postService.getSearch((any()), anyString(), anyString())).thenReturn(postResponseDtoList);

        mvc.perform(get("/search")
                        .param("keyword", "검색어")
                        .param("sort", "recent")
                        .characterEncoding("utf-8")
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].postId").value(postResponseDto.getPostId()))
                .andExpect(jsonPath("$.[0].title").value(postResponseDto.getTitle()))
                .andExpect(jsonPath("$.[0].headCount").value(postResponseDto.getHeadCount()))
                .andExpect(jsonPath("$.[0].restaurant").value(postResponseDto.getRestaurant()))
                .andExpect(jsonPath("$.[0].orderTime").value(postResponseDto.getOrderTime()))
                .andExpect(jsonPath("$.[0].contents").value(postResponseDto.getContents()))
                .andExpect(jsonPath("$.[0].category").value(postResponseDto.getCategory()))
                .andExpect(jsonPath("$.[0].address").value(postResponseDto.getAddress()))
                .andExpect(jsonPath("$.[0].distance").value(postResponseDto.getDistance()))
                .andExpect(jsonPath("$.[0].userId").value(postResponseDto.getUserId()))
                .andExpect(jsonPath("$.[0].username").value(postResponseDto.getUsername()))
                .andExpect(jsonPath("$.[0].profileImg").value(postResponseDto.getProfileImg()))
                .andExpect(jsonPath("$.[0].createdAt").value(postResponseDto.getCreatedAt()))
                .andExpect(jsonPath("$.[0].roomId").value(postResponseDto.getRoomId()))
                .andExpect(jsonPath("$.[0].nowHeadCount").value(postResponseDto.getNowHeadCount()))
                .andExpect(jsonPath("$.[0].valid").value(postResponseDto.getValid()))
                .andDo(MockMvcResultHandlers.print());

        verify(postService, times(1)).getSearch(any(), anyString(), anyString());
    }

    @Test
    @DisplayName("유저 근처에 작성된 게시글 조회")
    void getPostByUserDist() throws Exception {
        when(postService.getPostByUserDist(any(), anyString(), anyString())).thenReturn(postResponseDtoList);

        mvc.perform(get("/posts/around")
                        .param("category", "전체")
                        .param("sort", "recent")
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].postId").value(postResponseDto.getPostId()))
                .andExpect(jsonPath("$.[0].title").value(postResponseDto.getTitle()))
                .andExpect(jsonPath("$.[0].headCount").value(postResponseDto.getHeadCount()))
                .andExpect(jsonPath("$.[0].restaurant").value(postResponseDto.getRestaurant()))
                .andExpect(jsonPath("$.[0].orderTime").value(postResponseDto.getOrderTime()))
                .andExpect(jsonPath("$.[0].contents").value(postResponseDto.getContents()))
                .andExpect(jsonPath("$.[0].category").value(postResponseDto.getCategory()))
                .andExpect(jsonPath("$.[0].address").value(postResponseDto.getAddress()))
                .andExpect(jsonPath("$.[0].distance").value(postResponseDto.getDistance()))
                .andExpect(jsonPath("$.[0].userId").value(postResponseDto.getUserId()))
                .andExpect(jsonPath("$.[0].username").value(postResponseDto.getUsername()))
                .andExpect(jsonPath("$.[0].profileImg").value(postResponseDto.getProfileImg()))
                .andExpect(jsonPath("$.[0].createdAt").value(postResponseDto.getCreatedAt()))
                .andExpect(jsonPath("$.[0].roomId").value(postResponseDto.getRoomId()))
                .andExpect(jsonPath("$.[0].nowHeadCount").value(postResponseDto.getNowHeadCount()))
                .andExpect(jsonPath("$.[0].valid").value(postResponseDto.getValid()))
                .andDo(MockMvcResultHandlers.print());

        verify(postService, times(1)).getPostByUserDist(any(), anyString(), anyString());
    }

}