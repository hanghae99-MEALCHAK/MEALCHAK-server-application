package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.PostDetailResponseDto;
import com.mealchak.mealchakserverapplication.dto.response.PostResponseDto;
import com.mealchak.mealchakserverapplication.model.*;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.MenuRepository;
import com.mealchak.mealchakserverapplication.repository.PostQueryRepository;
import com.mealchak.mealchakserverapplication.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PostQueryRepository postQueryRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private AllChatInfoService allChatInfoService;


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
        User user01 = new User(100L, 101L, "user01", "pw", "user01@test.com", "https://gorokke.shop/image/profileDefaultImg.jpg",
                "10", "female", "comment11", 5F, location01);
        userDetails01 = new UserDetailsImpl(user01);
        // 사용자 user01 의 post01
        Menu cafe = new Menu("카페", 1);
        chatRoom01 = new ChatRoom("UUID111", userDetails01.getUser());
        post01 = new Post(100L, "title", 3, "restaurant01", "2021-09-01 00:00:00",
                "contents", true, false, chatRoom01, userDetails01.getUser(), cafe, location01,
                2.00, 1L, Post.meetingType.SEPARATE);

        postRequestDto = new PostRequestDto("title", 3,
                "서울특별시 강남구", 37.111111, 126.111111, "restaurant01",
                "2021-09-01 00:00:00", "contents", "카페", Post.meetingType.SEPARATE);

        // 사용자 존재 user02
        Location locationUser02 = new Location("부산시 사하구", 37.222222, 126.222222);
        User user02 = new User(200L, 202L, "user02", "pw", "user02@test.com", "https://gorokke.shop/image/profileDefaultImg.jpg",
                "20", "female", "comment22", 6F, locationUser02);
        userDetails02 = new UserDetailsImpl(user02);
        // 사용자 user01 의 post01
        Menu koreanFood = new Menu("한식", 1);
        chatRoom02 = new ChatRoom("UUID222", userDetails02.getUser());
        Location locationPost02 = new Location("부산시 사하구", 37.222200, 126.222200);
        post02 = new Post(200L, "title", 3, "restaurant02", "2021-09-01 00:00:00",
                "contents", true, false, chatRoom02, userDetails02.getUser(), koreanFood, locationPost02,
                0.003, 1L, Post.meetingType.SEPARATE);

    }

    void compareAssertPostResponseDtoListAndPost01(List<PostResponseDto> results) {
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
    @DisplayName("모집글생성_사용자가_Null_일때_IAE발생")
    public void createPostTest01() {
        // then
        ChatRoom chatRoom = new ChatRoom();
        // when

        // then
        assertThrows(IllegalArgumentException.class,
                () -> postService.createPost(userDetailsNull, postRequestDto, chatRoom), "로그인하지 않았습니다.");
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
        Menu menu = new Menu(postRequestDto.getCategory(), 1);
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

    @Test
    @DisplayName("모집글상세조회시PostId가없으면_IAE발생")
    public void getPostDetail01() throws Exception {
        // given

        // mocking
        when(postQueryRepository.findById(post01.getId())).thenReturn(null);
        // when

        //then
        assertThrows(IllegalArgumentException.class,
                () -> postService.getPostDetail(post01.getId()), "존재하지 않는 게시글입니다.");
    }

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
        List<PostResponseDto> results = postService.getMyPost(userDetails01);
        //then
        verify(postQueryRepository).findByUser_IdOrderByCreatedAtDesc(userDetails01.getUser().getId());
        compareAssertPostResponseDtoListAndPost01(results);

    }

    @Test
    @DisplayName("모집글_수정시_메뉴가_같을때")
    void updatePostDetail01() throws Exception {
        // given
        PostRequestDto updateDto = new PostRequestDto("updatetitle", 4,
                "서울시 강남구 update", 37.24352, 126.87986, "updaterestaurant",
                "2021-08-01 11:11:11", "updatecontents", "카페", Post.meetingType.SEPARATE);

        // mocking
        when(postQueryRepository.findByIdAndUserId(post01.getId(), userDetails01.getUser().getId())).thenReturn(post01);
        // when
        PostResponseDto postResponseDto = postService.updatePostDetail(post01.getId(), updateDto, userDetails01);
        // then
        verify(postQueryRepository, times(1)).findByIdAndUserId(post01.getId(), userDetails01.getUser().getId());
        verify(menuRepository, never()).findByCategory(updateDto.getCategory());

        assertThat(post01.getMenu().getCount()).isEqualTo(1);
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
                "2021-08-01 11:11:11", "updatecontents", "분식", Post.meetingType.SEPARATE);
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
                "2021-08-01 11:11:11", "updatecontents", "분식", Post.meetingType.SEPARATE);
        Menu menu = new Menu("분식", 1);

        // mocking
        when(postQueryRepository.findByIdAndUserId(post01.getId(), userDetails01.getUser().getId())).thenReturn(post01);
        when(menuRepository.findByCategory(updateDto.getCategory())).thenReturn(Optional.empty());
        // when
        // then
        assertThrows(IllegalArgumentException.class,
                () -> postService.updatePostDetail(post01.getId(), updateDto, userDetails01), "메뉴가 존재하지 않습니다");
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

        compareAssertPostResponseDtoListAndPost01(results);
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
        compareAssertPostResponseDtoListAndPost01(results);

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
        compareAssertPostResponseDtoListAndPost01(results);

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
                () -> postService.getSearch(userDetails01, keyword, sort), "잘못된 sort 요청입니다.");
    }

    @Test
    @DisplayName("유저근처_검색시_미로그인_상태일때_getAllPst가_실행됨")
    public void getPostByUserDist01() throws Exception {
        // given
        String category = "전체";
        String sort = "recent";

        List<Post> posts = new ArrayList<>();
        posts.add(post01);
        // mocking
        when(postQueryRepository.findAllOrderByOrderTimeAsc()).thenReturn(posts);

        // when
        List<PostResponseDto> results = postService.getPostByUserDist(userDetailsNull, category, sort);
        //then
        compareAssertPostResponseDtoListAndPost01(results);

    }

    @Test
    @DisplayName("유저근처_검색시_sort가_recent일때_getAllPostSortByRecent가_실행됨")
    public void getPostByUserDist02() throws Exception {
        // given
        String category = "전체";
        String sort = "recent";

        List<Post> posts = new ArrayList<>();
        posts.add(post01);
        // mocking
        when(postQueryRepository.findByLocationOrderByOrderTimeAsc(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(posts);

        // when
        List<PostResponseDto> results = postService.getPostByUserDist(userDetails01, category, sort);
        //then
        verify(postQueryRepository, times(1)).findByLocationOrderByOrderTimeAsc(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        compareAssertPostResponseDtoListAndPost01(results);

    }

    @Test
    @DisplayName("유저근처_검색시_sort가_nearBy일때_getPostByCategory가_실행됨")
    public void getPostByUserDist03() throws Exception {
        // given
        String category = "전체";
        String sort = "nearBy";

        List<Post> posts = new ArrayList<>();
        posts.add(post01);
        // mocking
        when(postQueryRepository.findByLocationOrderByOrderTimeAsc(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(posts);

        // when
        List<PostResponseDto> results = postService.getPostByUserDist(userDetails01, category, sort);
        //then
        verify(postQueryRepository, times(1)).findByLocationOrderByOrderTimeAsc(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        compareAssertPostResponseDtoListAndPost01(results);
    }

    @Test
    @DisplayName("유저근처_검색시_sort가_잘못된_요청일때_IAE발생")
    public void getPostByUserDist04() throws Exception {
        // given
        String category = "전체";
        String sort = "aaaaa";

        // mocking

        // when
        assertThrows(IllegalArgumentException.class,
                () -> postService.getPostByUserDist(userDetails01, category, sort), "잘못된 sort 요청입니다.");
        //then
    }

    @Test
    @DisplayName("비회원검색")
    public void getSearchPost01() throws Exception {
        // given
        String keyword = "keyword";

        List<Post> posts = new ArrayList<>();
        posts.add(post01);
        // mocking
        when(postQueryRepository.findBySearchKeywordOrderByOrderTimeAsc(keyword)).thenReturn(posts);
        // when
        List<PostResponseDto> results = postService.getSearchPost(keyword);
        //then
        verify(postQueryRepository, times(1)).findBySearchKeywordOrderByOrderTimeAsc(keyword);
        compareAssertPostResponseDtoListAndPost01(results);
    }

    @Test
    @DisplayName("회원검색(모집_마감임박순)")
    public void getSearchPostBySortByRecent() throws Exception {
        // given
        String keyword = "keyword";

        List<Post> posts = new ArrayList<>();
        posts.add(post01);
        // mocking
        when(postQueryRepository.findBySearchKeywordOrderByOrderTimeAsc(keyword)).thenReturn(posts);
        // when
        List<PostResponseDto> results = postService.getSearchPostBySortByRecent(keyword, userDetails01.getUser());
        //then
        verify(postQueryRepository, times(1)).findBySearchKeywordOrderByOrderTimeAsc(keyword);
        compareAssertPostResponseDtoListAndPost01(results);
    }

    @Test
    @DisplayName("회원검색(거리순)")
    public void getSearchPostByUserDist() throws Exception {
        // given
        String keyword = "keyword";
        List<Post> posts = new ArrayList<>();
        posts.add(post01);
        // mocking
        when(postQueryRepository.findBySearchKeyword(keyword)).thenReturn(posts);
        // when
        List<PostResponseDto> results = postService.getSearchPostByUserDist(keyword, userDetails01.getUser());
        //then
        verify(postQueryRepository, times(1)).findBySearchKeyword(keyword);
        compareAssertPostResponseDtoListAndPost01(results);
    }

    @Test
    @DisplayName("거리순_모집글_결과리스트_내려주기")
    public void getNearByResult() throws Exception {
        // given
        List<Post> posts = new ArrayList<>();
        posts.add(post01);
        // when
        List<PostResponseDto> results = postService.getNearByResult(posts, userDetails01.getUser());
        //then
        compareAssertPostResponseDtoListAndPost01(results);
    }

    @Test
    @DisplayName("모집글_최신순_조회")
    public void getAllPostSortByRecent() throws Exception {
        // given
        String category = "전체";
        User user = userDetails01.getUser();

        List<Post> posts = new ArrayList<>();
        posts.add(post01);

        // mocking
        when(postQueryRepository.findByLocationOrderByOrderTimeAsc(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(posts);
        // when
        List<PostResponseDto> results = postService.getAllPostSortByRecent(user,
                user.getLocation().getLatitude(), user.getLocation().getLatitude(), category);
        //then
        verify(postQueryRepository, times(1)).findByLocationOrderByOrderTimeAsc(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        compareAssertPostResponseDtoListAndPost01(results);

    }

    @Test
    @DisplayName("게시글에_거리_표시하기")
    public void getPostsDistance() throws Exception {
        // given
        User user = userDetails01.getUser();
        List<Post> posts = new ArrayList<>();
        posts.add(post01);

        // when
        List<PostResponseDto> results = ReflectionTestUtils.invokeMethod(postService, "getPostsDistance", user, posts);

        //then
        compareAssertPostResponseDtoListAndPost01(results);

    }

    @Test
    @DisplayName("사용자_반경_3Km_게시물_조회시_category가_전체일때")
    public void getPostByCategory01() throws Exception {
        // given
        String category = "전체";
        Location location =  userDetails01.getUser().getLocation();

        List<Post> posts = new ArrayList<>();
        posts.add(post01);

        // mocking
        when(postQueryRepository.findByLocationOrderByOrderTimeAsc(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(posts);
        // when
        postService.getPostByCategory(category, location.getLatitude(), location.getLongitude());
        //then
        verify(postQueryRepository, times(1)).findByLocationOrderByOrderTimeAsc(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        verify(postQueryRepository, never()).findByLocationAndCategoryOrderByOrderTimeAsc(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyString());

    }


    @Test
    @DisplayName("사용자_반경_3Km_게시물_조회시_category가_전체이외_일때")
    public void getPostByCategory02() throws Exception {
        // given
        String category = "기타";
        Location location =  userDetails01.getUser().getLocation();

        List<Post> posts = new ArrayList<>();
        posts.add(post01);

        // mocking
        when(postQueryRepository.findByLocationAndCategoryOrderByOrderTimeAsc(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyString()))
                .thenReturn(posts);
        // when
        postService.getPostByCategory(category, location.getLatitude(), location.getLongitude());
        //then
        verify(postQueryRepository, times(1)).findByLocationAndCategoryOrderByOrderTimeAsc(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyString());
        verify(postQueryRepository, never()).findByLocationOrderByOrderTimeAsc(anyDouble(), anyDouble(), anyDouble(), anyDouble());

    }

    @Test
    @DisplayName("거리_계산_로직")
    public void getDist() throws Exception {
        // given
        User user = userDetails02.getUser();

        // when
        Double result = ReflectionTestUtils.invokeMethod(postService, "getDist", user, post02);

        //then
        assertThat(result).isEqualTo(post02.getDistance());
    }

    @Test
    @DisplayName("post_찾지_못했을때_IAE발생")
    public void getPost01() throws Exception {
        // given
        when(postQueryRepository.findById(post01.getId())).thenReturn(null);
        // when

        //then
        assertThrows(IllegalArgumentException.class,
                () -> postService.getPost(post01.getId()), "존재하지 않는 게시글입니다.");
    }

    @Test
    @DisplayName("post_찾기_성공")
    public void getPost02() throws Exception {
        // given
        when(postQueryRepository.findById(post01.getId())).thenReturn(post01);
        // when
        Post post = postService.getPost(post01.getId());
        //then
        verify(postQueryRepository, times(1)).findById(post01.getId());
        assertThat(post.getId()).isEqualTo(post01.getId());
        assertThat(post.getTitle()).isEqualTo(post01.getTitle());
        assertThat(post.getHeadCount()).isEqualTo(post01.getHeadCount());
        assertThat(post.getRestaurant()).isEqualTo(post01.getRestaurant());
        assertThat(post.getOrderTime()).isEqualTo(post01.getOrderTime());
        assertThat(post.getContents()).isEqualTo(post01.getContents());
        assertThat(post.isCheckValid()).isEqualTo(post01.isCheckValid());
        assertThat(post.isCheckDeleted()).isEqualTo(post01.isCheckDeleted());
        assertThat(post.getChatRoom()).isEqualTo(post01.getChatRoom());
        assertThat(post.getUser()).isEqualTo(post01.getUser());
        assertThat(post.getMenu()).isEqualTo(post01.getMenu());
        assertThat(post.getLocation()).isEqualTo(post01.getLocation());
        assertThat(post.getDistance()).isEqualTo(post01.getDistance());
        assertThat(post.getNowHeadCount()).isEqualTo(post01.getNowHeadCount());

    }
}