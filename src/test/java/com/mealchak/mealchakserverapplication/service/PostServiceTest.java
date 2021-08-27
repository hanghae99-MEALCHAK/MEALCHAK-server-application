package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.PostDetailResponseDto;
import com.mealchak.mealchakserverapplication.dto.response.PostResponseDto;
import com.mealchak.mealchakserverapplication.model.*;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.AllChatInfoQueryRepository;
import com.mealchak.mealchakserverapplication.repository.MenuRepository;
import com.mealchak.mealchakserverapplication.repository.PostQueryRepository;
import com.mealchak.mealchakserverapplication.repository.PostRepository;
import com.mealchak.mealchakserverapplication.service.AllChatInfoService;
import com.mealchak.mealchakserverapplication.service.ChatRoomService;
import com.mealchak.mealchakserverapplication.service.JoinRequestsService;
import com.mealchak.mealchakserverapplication.service.PostService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.relational.core.sql.TrueCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.anyOf;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostQueryRepository postQueryRepository;;

    @Mock
    private AllChatInfoService allChatInfoService;

    @Mock
    private AllChatInfoQueryRepository allChatInfoQueryRepository;

    @Mock
    private JoinRequestsService joinRequestsService;

    @Mock
    private ChatRoomService chatRoomService;

    private UserDetailsImpl userDetailsNull;
    private UserDetailsImpl userDetails01;
    private ChatRoom chatRoom01;
    private Post post01;
    private PostRequestDto postRequestDto;
    private UserDetailsImpl userDetails02;
    private ChatRoom chatRoom02;
    private Post post02;


    @BeforeEach
    void setUp() {
        // 사용자 Null
        userDetailsNull = null;
        // 사용자 존재 user01
        Location location01 = new Location("서울특별시 강남구", 37.111111, 126.111111);
        User user01 = new User(100L,101L, "user01", "pw", "user01@test.com", "https://gorokke.shop/image/profileDefaultImg.jpg",
                "10", "female", "comment11", 5F, location01);
        userDetails01 = new UserDetailsImpl(user01);
        // 사용자 user01 의 post01
        Menu cafe = new Menu("카페", 1);
        chatRoom01 = new ChatRoom("UUID111", userDetails01.getUser());
        post01 = new Post(100L, "title", 3, "restaurant01", "2021-09-01 00:00:00",
                "contents", true, false, chatRoom01, userDetails01.getUser(), cafe, location01,
                2.00, 1L);

        postRequestDto = new PostRequestDto("title", 3,
                "서울특별시 강남구", 37.111111, 126.111111, "restaurant01",
                "2021-09-01 00:00:00", "contents", "카페");

        // 사용자 존재 user02
        Location location02 = new Location("부산시 사하구", 37.222222, 126.222222);
        User user02 = new User(200L,202L, "user02", "pw", "user02@test.com", "https://gorokke.shop/image/profileDefaultImg.jpg",
                "20", "female", "comment22", 6F, location02);
        userDetails02 = new UserDetailsImpl(user02);
        // 사용자 user01 의 post01
        Menu koreanFood = new Menu("한식", 1);
        chatRoom02 = new ChatRoom("UUID222", userDetails02.getUser());
        post02 = new Post(200L, "title", 3, "restaurant02", "2021-09-01 00:00:00",
                "contents", true, false, chatRoom02, userDetails02.getUser(), koreanFood, location02,
                2.00, 1L);

    }

    @Test
    @DisplayName("모집글생성_사용자가_Null_일때_IAE발생")
    public void createPostTest01() {
        // then
        ChatRoom chatRoom = new ChatRoom();
        // when

        // then
        assertThrows(IllegalArgumentException.class,
                () -> postService.createPost(userDetailsNull, postRequestDto, chatRoom),"로그인하지 않았습니다.");
    }

    @Test
    @DisplayName("모집글생성_사용자의_응답에_메뉴가 존재하지 않을 경우")
    public void createPostTest02() throws Exception {
        // given
        Menu newMenu = new Menu(postRequestDto.getCategory(), 1);
        Post post = new Post();

        // mocking
        when(menuRepository.findByCategory(postRequestDto.getCategory()))
                .thenReturn(Optional.empty());

        // when
        postService.createPost(userDetails01, postRequestDto, chatRoom01);
        //then
        verify(menuRepository, times(1)).findByCategory(postRequestDto.getCategory());
        verify(menuRepository, times(1)).save(refEq(newMenu));
//        verify(postRepository, times(1)).save(refEq(post));
    }

    @Test
    @DisplayName("모집글생성_사용자의_응답에_메뉴가 존재할_경우")
    public void createPostTest03() throws Exception {
        // given
        Menu menu = new Menu(postRequestDto.getCategory(),1);
        Post post = new Post();

        // mocking
        when(menuRepository.findByCategory(postRequestDto.getCategory()))
                .thenReturn(Optional.of(menu));

        // when
        postService.createPost(userDetails01, postRequestDto, chatRoom01);

        //then
        verify(menuRepository, times(1)).findByCategory(postRequestDto.getCategory());
        assertThat(menu.getCount()).isEqualTo(2);
        assertThat(menu.getCategory()).isEqualTo(postRequestDto.getCategory());
//        verify(postRepository, times(1)).save(refEq(post));

    }

    @Test
    @DisplayName("모집글전체조회_카테고리가_전체일때")
    public void getAllPostTest01() throws Exception {
        // given
        List<Post> posts = new ArrayList<>();
        posts.add(post01);
        posts.add(post02);

        // Mocking
        when(postQueryRepository.findAllOrderByOrderTimeAsc()).thenReturn(posts);

        // when
        List<PostResponseDto> resultListPost = postService.getAllPost("전체");

        //then
        verify(postQueryRepository, times(1)).findAllOrderByOrderTimeAsc();
        verify(postQueryRepository, never()).findByMenu_CategoryOrderByOrderTimeAsc("전체");
        assertThat(resultListPost.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("모집글전체조회_카테고리가_전체이외_일때")
    public void getAllPostTest02() throws Exception {
        // given
        List<Post> posts = new ArrayList<>();
        posts.add(post02);

        // Mocking
        when(postQueryRepository.findByMenu_CategoryOrderByOrderTimeAsc("한식")).thenReturn(posts);

        // when
        List<PostResponseDto> resultListPost = postService.getAllPost("한식");

        //then
        verify(postQueryRepository, never()).findAllOrderByOrderTimeAsc();
        verify(postQueryRepository, times(1)).findByMenu_CategoryOrderByOrderTimeAsc("한식");
        assertThat(resultListPost.size()).isEqualTo(1);
        assertThat(resultListPost.get(0).getCategory()).isEqualTo("한식");

    }

//    @Test
//    @DisplayName("모집글_상세_조회시_PostId가_없으면_IAE발생")
//    public void getPostDetail01() throws Exception {
//        // given
//        Long postId = 100L;
//        // mocking
//
//        // when
//
//        //then
//        assertThrows(IllegalArgumentException.class,
//                () -> postService.getPostDetail(postId));
//    }

    @Test
    @DisplayName("모집글_상세_조회")
    public void getPostDetail02() throws Exception {
        // given
        Long postId = post01.getId();

        List<User> userList = new ArrayList<>();
        userList.add(userDetails01.getUser());

        // mocking
        when(postQueryRepository.findById(postId)).thenReturn(post01);
        when(allChatInfoService.getUser(post01.getChatRoom().getId())).thenReturn(userList);

        // when
        PostDetailResponseDto postDetail = postService.getPostDetail(postId);

        //then
        verify(allChatInfoService, times(1)).getUser(post01.getChatRoom().getId());
        assertThat(postDetail.getPostId()).isEqualTo(post01.getId());
        assertThat(postDetail.getTitle()).isEqualTo(post01.getTitle());
        assertThat(postDetail.getHeadCount()).isEqualTo(post01.getHeadCount());
        assertThat(postDetail.getRestaurant()).isEqualTo(post01.getRestaurant());
        assertThat(postDetail.getOrderTime()).isEqualTo(post01.getOrderTime());
        assertThat(postDetail.getContents()).isEqualTo(post01.getContents());
        assertThat(postDetail.getCategory()).isEqualTo(post01.getMenu().getCategory());
        assertThat(postDetail.getAddress()).isEqualTo(post01.getLocation().getAddress());
        assertThat(postDetail.getLatitude()).isEqualTo(post01.getLocation().getLatitude());
        assertThat(postDetail.getLongitude()).isEqualTo(post01.getLocation().getLongitude());
        assertThat(postDetail.getDistance()).isEqualTo(post01.getDistance());
        assertThat(postDetail.getUserId()).isEqualTo(post01.getUser().getId());
        assertThat(postDetail.getUsername()).isEqualTo(post01.getUser().getUsername());
        assertThat(postDetail.getProfileImg()).isEqualTo(post01.getUser().getProfileImg());
        assertThat(postDetail.getCreatedAt()).isEqualTo(post01.getCreatedAt());
        assertThat(postDetail.getRoomId()).isEqualTo(post01.getChatRoom().getId());
        assertThat(postDetail.getNowHeadCount()).isEqualTo(post01.getNowHeadCount());
        assertThat(postDetail.getUserList().size()).isEqualTo(1);

    }

    @Test
    @DisplayName("내가_쓴_글조회시_사용자가_Null_일때_IAE발생")
    public void getMyPost01() throws Exception {
        // given

        // when

        //then
        assertThrows(IllegalArgumentException.class,
                () -> postService.getMyPost(userDetailsNull), "로그인이 필요합니다.");
    }

    @Test
    @DisplayName("내가_쓴_글조회")
    public void getMyPost02() throws Exception {
        // given
        List<Post> posts = new ArrayList<>();
        posts.add(post01);

        // mocking
        when(postQueryRepository.findByUser_IdOrderByCreatedAtDesc(userDetails01.getUser().getId())).thenReturn(posts);
        // when
        List<PostResponseDto> resultPostList = postService.getMyPost(userDetails01);
        //then
        verify(postQueryRepository).findByUser_IdOrderByCreatedAtDesc(userDetails01.getUser().getId());
        assertThat(resultPostList.get(0).getPostId()).isEqualTo(post01.getId());
        assertThat(resultPostList.get(0).getTitle()).isEqualTo(post01.getTitle());
        assertThat(resultPostList.get(0).getContents()).isEqualTo(post01.getContents());
        assertThat(resultPostList.get(0).getHeadCount()).isEqualTo(post01.getHeadCount());
        assertThat(resultPostList.get(0).getCategory()).isEqualTo(post01.getMenu().getCategory());
        assertThat(resultPostList.get(0).getRestaurant()).isEqualTo(post01.getRestaurant());
        assertThat(resultPostList.get(0).getOrderTime()).isEqualTo(post01.getOrderTime());
        assertThat(resultPostList.get(0).getAddress()).isEqualTo(post01.getLocation().getAddress());
        assertThat(resultPostList.get(0).getDistance()).isEqualTo(post01.getDistance());
        assertThat(resultPostList.get(0).getUserId()).isEqualTo(post01.getUser().getId());
        assertThat(resultPostList.get(0).getUsername()).isEqualTo(post01.getUser().getUsername());
        assertThat(resultPostList.get(0).getProfileImg()).isEqualTo(post01.getUser().getProfileImg());
        assertThat(resultPostList.get(0).getCreatedAt()).isEqualTo(post01.getCreatedAt());
        assertThat(resultPostList.get(0).getRoomId()).isEqualTo(post01.getChatRoom().getId());
        assertThat(resultPostList.get(0).getNowHeadCount()).isEqualTo(post01.getNowHeadCount());
        assertThat(resultPostList.get(0).getValid()).isEqualTo(post01.isCheckValid());

    }

    @Test
    @DisplayName("모집글_수정시_메뉴가_같을때")
    void updatePostDetail01() throws Exception {
        // given
        PostRequestDto updateDto = new PostRequestDto("updatetitle", 4,
                "서울시 강남구 update", 37.24352, 126.87986, "updaterestaurant",
                "2021-08-01 11:11:11", "updatecontents", "카페");

        // mocking
        when(postQueryRepository.findByIdAndUserId(post01.getId(), userDetails01.getUser().getId())).thenReturn(post01);
        // when
        PostResponseDto postResponseDto = postService.updatePostDetail(post01.getId(), updateDto, userDetails01);
        // then
        verify(postQueryRepository, times(1)).findByIdAndUserId(post01.getId(), userDetails01.getUser().getId());
        verify(menuRepository, never()).findByCategory(updateDto.getCategory());

        assertThat(post01.getMenu().getCount()).isEqualTo(2);
        assertThat(post01.getMenu().getCategory()).isEqualTo(updateDto.getCategory());

        assertThat(postResponseDto.getPostId()).isEqualTo(post01.getId());
        assertThat(postResponseDto.getTitle()).isEqualTo(updateDto.getTitle());
        assertThat(postResponseDto.getContents()).isEqualTo(updateDto.getContents());
        assertThat(postResponseDto.getHeadCount()).isEqualTo(updateDto.getHeadCount());
        assertThat(postResponseDto.getCategory()).isEqualTo(updateDto.getCategory());
        assertThat(postResponseDto.getRestaurant()).isEqualTo(updateDto.getRestaurant());
        assertThat(postResponseDto.getOrderTime()).isEqualTo(updateDto.getOrderTime());
        assertThat(postResponseDto.getAddress()).isEqualTo(updateDto.getAddress());
        assertThat(postResponseDto.getDistance()).isEqualTo(post01.getDistance());
        assertThat(postResponseDto.getUserId()).isEqualTo(post01.getUser().getId());
        assertThat(postResponseDto.getUsername()).isEqualTo(post01.getUser().getUsername());
        assertThat(postResponseDto.getProfileImg()).isEqualTo(post01.getUser().getProfileImg());
        assertThat(postResponseDto.getCreatedAt()).isEqualTo(post01.getCreatedAt());
        assertThat(postResponseDto.getRoomId()).isEqualTo(post01.getChatRoom().getId());
        assertThat(postResponseDto.getNowHeadCount()).isEqualTo(post01.getNowHeadCount());
        assertThat(postResponseDto.getValid()).isEqualTo(post01.isCheckValid());
    }

    @Test
    @DisplayName("모집글_수정시_메뉴가_다를_때")
    void updatePostDetail02() throws Exception {
        // given
        PostRequestDto updateDto = new PostRequestDto("updatetitle", 4,
                "서울시 강남구 update", 37.24352, 126.87986, "updaterestaurant",
                "2021-08-01 11:11:11", "updatecontents", "분식");
        Menu menu = new Menu("분식", 1);

        // mocking
        when(postQueryRepository.findByIdAndUserId(post01.getId(), userDetails01.getUser().getId())).thenReturn(post01);
        when(menuRepository.findByCategory(updateDto.getCategory())).thenReturn(Optional.of(menu));
        // when
        PostResponseDto postResponseDto = postService.updatePostDetail(post01.getId(), updateDto, userDetails01);
        // then
        verify(postQueryRepository, times(1)).findByIdAndUserId(post01.getId(), userDetails01.getUser().getId());
        verify(menuRepository, times(1)).findByCategory(updateDto.getCategory());

        assertThat(post01.getMenu().getCount()).isEqualTo(2);
        assertThat(post01.getMenu().getCategory()).isEqualTo(updateDto.getCategory());

        assertThat(postResponseDto.getPostId()).isEqualTo(post01.getId());
        assertThat(postResponseDto.getTitle()).isEqualTo(updateDto.getTitle());
        assertThat(postResponseDto.getContents()).isEqualTo(updateDto.getContents());
        assertThat(postResponseDto.getHeadCount()).isEqualTo(updateDto.getHeadCount());
        assertThat(postResponseDto.getCategory()).isEqualTo(updateDto.getCategory());
        assertThat(postResponseDto.getRestaurant()).isEqualTo(updateDto.getRestaurant());
        assertThat(postResponseDto.getOrderTime()).isEqualTo(updateDto.getOrderTime());
        assertThat(postResponseDto.getAddress()).isEqualTo(updateDto.getAddress());
        assertThat(postResponseDto.getDistance()).isEqualTo(post01.getDistance());
        assertThat(postResponseDto.getUserId()).isEqualTo(post01.getUser().getId());
        assertThat(postResponseDto.getUsername()).isEqualTo(post01.getUser().getUsername());
        assertThat(postResponseDto.getProfileImg()).isEqualTo(post01.getUser().getProfileImg());
        assertThat(postResponseDto.getCreatedAt()).isEqualTo(post01.getCreatedAt());
        assertThat(postResponseDto.getRoomId()).isEqualTo(post01.getChatRoom().getId());
        assertThat(postResponseDto.getNowHeadCount()).isEqualTo(post01.getNowHeadCount());
        assertThat(postResponseDto.getValid()).isEqualTo(post01.isCheckValid());
    }

    @Test
    @DisplayName("모집글_수정시_메뉴가_다를_때_메뉴를_못찾아서_IAE_발생")
    void updatePostDetail03() throws Exception {
        // given
        PostRequestDto updateDto = new PostRequestDto("updatetitle", 4,
                "서울시 강남구 update", 37.24352, 126.87986, "updaterestaurant",
                "2021-08-01 11:11:11", "updatecontents", "분식");
        Menu menu = new Menu("분식", 1);

        // mocking
        when(postQueryRepository.findByIdAndUserId(post01.getId(), userDetails01.getUser().getId())).thenReturn(post01);
        when(menuRepository.findByCategory(updateDto.getCategory())).thenReturn(Optional.empty());
        // when
        // then
        assertThrows(IllegalArgumentException.class,
                () -> postService.updatePostDetail(post01.getId(), updateDto, userDetails01),"메뉴가 존재하지 않습니다");
    }

    @Test
    @DisplayName("모집글_삭제시_삭제권한이_없을_때")
    public void deletePost01() throws Exception {
        // given

        // mocking
        when(postQueryRepository.findById(post01.getId())).thenReturn(post01);

        // when
       assertThrows(IllegalArgumentException.class,
                () -> postService.deletePost(post01.getId(), userDetails02), "삭제 권한이 없습니다.");
        //then
    }

    @Test
    @DisplayName("모집글_삭제")
    public void deletePost02() throws Exception {
        // given

        // mocking
        when(postQueryRepository.findById(post01.getId())).thenReturn(post01);
        // when
        postService.deletePost(post01.getId(), userDetails01);

        // then
        verify(postQueryRepository, times(1)).findById(post01.getId());
        verify(joinRequestsService, times(1)).deleteByPostId(post01.getId());
        verify(chatRoomService, times(1)).deleteAllChatInfo(post01.getChatRoom().getId(), userDetails01);

        assertThat(post01.getMenu().getCount()).isEqualTo(0);
        assertThat(post01.isCheckDeleted()).isEqualTo(true);
        assertThat(post01.isCheckValid()).isEqualTo(false);

    }

    @Test
    @DisplayName("검색시_미로그인_상태일때_getSearchPost가_실행됨")
    public void getSearch01() throws Exception {
        // given
        List<Post> posts = new ArrayList<>();
        posts.add(post01);

        String keyword = "검색어";

        // mocking
        when(postQueryRepository.findBySearchKeywordOrderByOrderTimeAsc(keyword)).thenReturn(posts);

        // when
        List<PostResponseDto> results = postService.getSearch(userDetailsNull, keyword, "recent");
        //then
        verify(postQueryRepository, times(1)).findBySearchKeywordOrderByOrderTimeAsc(keyword);

        assertThat(results.get(0).getPostId()).isEqualTo(post01.getId());
        assertThat(results.get(0).getTitle()).isEqualTo(post01.getTitle());
        assertThat(results.get(0).getContents()).isEqualTo(post01.getContents());
        assertThat(results.get(0).getHeadCount()).isEqualTo(post01.getHeadCount());
        assertThat(results.get(0).getCategory()).isEqualTo(post01.getMenu().getCategory());
        assertThat(results.get(0).getRestaurant()).isEqualTo(post01.getRestaurant());
        assertThat(results.get(0).getOrderTime()).isEqualTo(post01.getOrderTime());
        assertThat(results.get(0).getAddress()).isEqualTo(post01.getLocation().getAddress());
        assertThat(results.get(0).getDistance()).isEqualTo(post01.getDistance());
        assertThat(results.get(0).getUserId()).isEqualTo(post01.getUser().getId());
        assertThat(results.get(0).getUsername()).isEqualTo(post01.getUser().getUsername());
        assertThat(results.get(0).getProfileImg()).isEqualTo(post01.getUser().getProfileImg());
        assertThat(results.get(0).getCreatedAt()).isEqualTo(post01.getCreatedAt());
        assertThat(results.get(0).getRoomId()).isEqualTo(post01.getChatRoom().getId());
        assertThat(results.get(0).getNowHeadCount()).isEqualTo(post01.getNowHeadCount());
        assertThat(results.get(0).getValid()).isEqualTo(post01.isCheckValid());

    }

    @Test
    @DisplayName("검색시_sort가_recent일때_getSearchPostBySortByRecent가_실행됨")
    public void getSearch02() throws Exception {
        // given
        String sort = "recent";
        String keyword = "검색어";

        List<Post> posts = new ArrayList<>();
        posts.add(post01);

        // mocking
        when(postQueryRepository.findBySearchKeywordOrderByOrderTimeAsc(keyword)).thenReturn(posts);
        // when
        List<PostResponseDto> results = postService.getSearch(userDetails01, keyword, sort);

        //then
        verify(postQueryRepository, times(1)).findBySearchKeywordOrderByOrderTimeAsc(keyword);
        verify(postQueryRepository, never()).findBySearchKeyword(keyword);
        assertThat(results.get(0).getPostId()).isEqualTo(post01.getId());
        assertThat(results.get(0).getTitle()).isEqualTo(post01.getTitle());
        assertThat(results.get(0).getContents()).isEqualTo(post01.getContents());
        assertThat(results.get(0).getHeadCount()).isEqualTo(post01.getHeadCount());
        assertThat(results.get(0).getCategory()).isEqualTo(post01.getMenu().getCategory());
        assertThat(results.get(0).getRestaurant()).isEqualTo(post01.getRestaurant());
        assertThat(results.get(0).getOrderTime()).isEqualTo(post01.getOrderTime());
        assertThat(results.get(0).getAddress()).isEqualTo(post01.getLocation().getAddress());
        assertThat(results.get(0).getDistance()).isEqualTo(post01.getDistance());
        assertThat(results.get(0).getUserId()).isEqualTo(post01.getUser().getId());
        assertThat(results.get(0).getUsername()).isEqualTo(post01.getUser().getUsername());
        assertThat(results.get(0).getProfileImg()).isEqualTo(post01.getUser().getProfileImg());
        assertThat(results.get(0).getCreatedAt()).isEqualTo(post01.getCreatedAt());
        assertThat(results.get(0).getRoomId()).isEqualTo(post01.getChatRoom().getId());
        assertThat(results.get(0).getNowHeadCount()).isEqualTo(post01.getNowHeadCount());
        assertThat(results.get(0).getValid()).isEqualTo(post01.isCheckValid());

    }

    @Test
    @DisplayName("검색시_sort가_nearBy일때_getSearchPostByUserDist가_실행됨")
    public void getSearch03() throws Exception {
        // given
        String sort = "nearBy";
        String keyword = "검색어";

        List<Post> posts = new ArrayList<>();
        posts.add(post01);

        // mocking
        when(postQueryRepository.findBySearchKeyword(keyword)).thenReturn(posts);
        // when
        List<PostResponseDto> results = postService.getSearch(userDetails01, keyword, sort);

        //then
        verify(postQueryRepository, times(1)).findBySearchKeyword(keyword);
        verify(postQueryRepository, never()).findBySearchKeywordOrderByOrderTimeAsc(keyword);
        assertThat(results.get(0).getPostId()).isEqualTo(post01.getId());
        assertThat(results.get(0).getTitle()).isEqualTo(post01.getTitle());
        assertThat(results.get(0).getContents()).isEqualTo(post01.getContents());
        assertThat(results.get(0).getHeadCount()).isEqualTo(post01.getHeadCount());
        assertThat(results.get(0).getCategory()).isEqualTo(post01.getMenu().getCategory());
        assertThat(results.get(0).getRestaurant()).isEqualTo(post01.getRestaurant());
        assertThat(results.get(0).getOrderTime()).isEqualTo(post01.getOrderTime());
        assertThat(results.get(0).getAddress()).isEqualTo(post01.getLocation().getAddress());
        assertThat(results.get(0).getDistance()).isEqualTo(post01.getDistance());
        assertThat(results.get(0).getUserId()).isEqualTo(post01.getUser().getId());
        assertThat(results.get(0).getUsername()).isEqualTo(post01.getUser().getUsername());
        assertThat(results.get(0).getProfileImg()).isEqualTo(post01.getUser().getProfileImg());
        assertThat(results.get(0).getCreatedAt()).isEqualTo(post01.getCreatedAt());
        assertThat(results.get(0).getRoomId()).isEqualTo(post01.getChatRoom().getId());
        assertThat(results.get(0).getNowHeadCount()).isEqualTo(post01.getNowHeadCount());
        assertThat(results.get(0).getValid()).isEqualTo(post01.isCheckValid());

    }

    @Test
    @DisplayName("검색시_sort가_잘못된_요청일때_IAE발생")
    public void getSearch04() throws Exception {
        // given
        String sort = "aaaaa";
        String keyword = "검색어";

        List<Post> posts = new ArrayList<>();
        posts.add(post01);

        // mocking

        // when

        //then
        verify(postQueryRepository, never()).findBySearchKeyword(keyword);
        verify(postQueryRepository, never()).findBySearchKeywordOrderByOrderTimeAsc(keyword);
        assertThrows(IllegalArgumentException.class,
                () ->  postService.getSearch(userDetails01, keyword, sort), "잘못된 sort 요청입니다.");
    }

    @Test
    @DisplayName("")
    public void getPostByUserDist() throws Exception {
        // given

        // when

        //then
    }
}
