package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.response.MyAwaitRequestJoinResponseDto;
import com.mealchak.mealchakserverapplication.dto.response.UserInfoAndPostResponseDto;
import com.mealchak.mealchakserverapplication.model.*;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.*;
import com.mealchak.mealchakserverapplication.repository.mapping.UserInfoMapping;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoinRequestsServiceTest {
    
    @InjectMocks
    private JoinRequestsService joinRequestsService;

    @Mock
    private JoinRequestsRepository joinRequestsRepository;

    @Mock
    private PostQueryRepository postQueryRepository;

    @Mock
    private AllChatInfoQueryRepository allChatInfoQueryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private AllChatInfoRepository allChatInfoRepository;

    private UserDetailsImpl userDetails01;
    private UserDetailsImpl userDetails02;
    private Post post;
    private ChatRoom chatRoom;
    private JoinRequests joinRequests;

    @BeforeEach
    void setUp() {
        // 사용자 user01
        Location location01 = new Location("서울특별시 강남구", 37.111111, 126.111111);
        User user01 = new User(100L, 101L, "user01", "pw", "user01@test.com", "https://gorokke.shop/image/profileDefaultImg.jpg",
                "10", "female", "comment11", 5F, location01);
        userDetails01 = new UserDetailsImpl(user01);

        // 사용자 user02
        Location locationUser02 = new Location("부산시 사하구", 37.222222, 126.222222);
        User user02 = new User(200L, 202L, "user02", "pw", "user02@test.com", "https://gorokke.shop/image/profileDefaultImg.jpg",
                "20", "female", "comment22", 6F, locationUser02);
        userDetails02 = new UserDetailsImpl(user02);

        // post room 관련
        Menu cafe = new Menu("카페", 1);
        post = new Post();
        chatRoom = new ChatRoom(111L, "UUID", user01.getId(), true, post);
        post = new Post(100L, "title", 3, "restaurant01", "2021-09-01 00:00:00",
                "contents", true, false, chatRoom, user01, cafe, location01,
                2.00, 1L, Post.meetingType.SEPARATE);
        chatRoom = new ChatRoom(111L, "UUID", user01.getId(), true, post);

      joinRequests = new JoinRequests(200L, userDetails02.getUser().getId(), post.getId(), userDetails01.getUser().getId());

    }

    @Test
    @DisplayName("입장신청시_신청_중복")
    void requestJoin01() throws Exception {
        // given

        // mocking
        when(joinRequestsRepository.findByUserIdAndPostId(userDetails02.getUser().getId(), post.getId()))
                .thenReturn(joinRequests);
        // when
        String result = joinRequestsService.requestJoin(userDetails02, post.getId());
        //then
        verify(joinRequestsRepository, times(1))
                .findByUserIdAndPostId(userDetails02.getUser().getId(), post.getId());
        assertThat(result).isEqualTo("이미 신청한 글입니다");
    }

    @Test
    @DisplayName("입장신청시_정상_신청")
    void requestJoin02() throws Exception {
        // given
        JoinRequests joinRequests = new JoinRequests(userDetails02.getUser().getId(), post.getId(), userDetails01.getUser().getId());
        // mocking
        when(joinRequestsRepository.findByUserIdAndPostId(userDetails02.getUser().getId(), post.getId()))
                .thenReturn(null);
        when(postQueryRepository.findByCheckValidTrueAndId(post.getId()))
                .thenReturn(post);
        when(allChatInfoQueryRepository.findByChatRoom_IdAndUser_Id(chatRoom.getId(), userDetails02.getUser().getId()))
                .thenReturn(null);
        // when
        String result = joinRequestsService.requestJoin(userDetails02, post.getId());
        //then
        verify(joinRequestsRepository, times(1))
                .findByUserIdAndPostId(userDetails02.getUser().getId(), post.getId());
        verify(postQueryRepository, times(1))
                .findByCheckValidTrueAndId(post.getId());
        verify(allChatInfoQueryRepository, times(1))
                .findByChatRoom_IdAndUser_Id(chatRoom.getId(), userDetails02.getUser().getId());
        verify(joinRequestsRepository, times(1))
                .save(refEq(joinRequests));
        assertThat(result).isEqualTo("신청완료");
    }

    @Test
    @DisplayName("입장신청시_이미_입장한방인_경우")
    void requestJoin03() throws Exception {
        // given
        AllChatInfo allChatInfo = new AllChatInfo();
        // mocking
        when(joinRequestsRepository.findByUserIdAndPostId(userDetails02.getUser().getId(), post.getId()))
                .thenReturn(null);
        when(postQueryRepository.findByCheckValidTrueAndId(post.getId()))
                .thenReturn(post);
        when(allChatInfoQueryRepository.findByChatRoom_IdAndUser_Id(chatRoom.getId(), userDetails02.getUser().getId()))
                .thenReturn(allChatInfo);
        // when
        String result = joinRequestsService.requestJoin(userDetails02, post.getId());
        //then
        verify(joinRequestsRepository, times(1))
                .findByUserIdAndPostId(userDetails02.getUser().getId(), post.getId());
        verify(postQueryRepository, times(1))
                .findByCheckValidTrueAndId(post.getId());
        verify(allChatInfoQueryRepository, times(1))
                .findByChatRoom_IdAndUser_Id(chatRoom.getId(), userDetails02.getUser().getId());
        assertThat(result).isEqualTo("이미 속해있는 채팅방입니다");
    }

//    @Test
//    @DisplayName("신청대기리스트_불러올때_")
//    void requestJoinList01() throws Exception {
//        // given
//        List<JoinRequests> joinRequestsList = new ArrayList<>();
//        JoinRequests joinRequests = new JoinRequests(200L, userDetails02.getUser().getId(), post.getId(), userDetails01.getUser().getId());
//        joinRequestsList.add(joinRequests);
//
//        // mocking
//        when(joinRequestsRepository.findByOwnUserId(userDetails01.getUser().getId()))
//                .thenReturn(joinRequestsList);
//        when(userRepository.findById(joinRequests.getUserId(), UserInfoMapping.class))
//                .thenReturn();
//        // when
//        List<UserInfoAndPostResponseDto> results = joinRequestsService.requestJoinList(userDetails01);
//        //then
//        verify(joinRequestsRepository, times(1))
//                .findByOwnUserId(userDetails02.getUser().getId());
//    }

    @Test
    @DisplayName("신청_대기_리스트")
   void myAwaitRequestJoinList() throws Exception {
        // given
        List<JoinRequests> joinRequestsList = new ArrayList<>();
        joinRequestsList.add(joinRequests);
        // mocking
        when(joinRequestsRepository.findByUserId(userDetails02.getUser().getId()))
                .thenReturn(joinRequestsList);
        when(postQueryRepository.findById(post.getId()))
                .thenReturn(post);
        // when
        List<MyAwaitRequestJoinResponseDto> results = joinRequestsService.myAwaitRequestJoinList(userDetails02);
        //then
        verify(joinRequestsRepository, times(1))
                .findByUserId(userDetails02.getUser().getId());
        verify(postQueryRepository, times(1))
                .findById(post.getId());
        assertThat(results.get(0).getJoinRequestId()).isEqualTo(userDetails02.getUser().getId());
        assertThat(results.get(0).getPostTitle()).isEqualTo((post.getTitle()));
    }
    
    @Test
    @DisplayName("입장_신청목록_불러오기실패로_IAE발생")
    void acceptJoinRequest01() throws Exception {
        // given

        // mocking
        when(joinRequestsRepository.findById(joinRequests.getId())).thenReturn(Optional.empty());
        // when

        //then
        assertThrows(IllegalArgumentException.class,
                () -> joinRequestsService.acceptJoinRequest(joinRequests.getId(), true),
                "존재하지 않는 신청입니다.");
        verify(joinRequestsRepository, times(1))
                .findById(joinRequests.getId());
    }

    @Test
    @DisplayName("입장_신청_거절")
    void acceptJoinRequest02() throws Exception {
        // given

        // mocking
        when(joinRequestsRepository.findById(joinRequests.getId())).thenReturn(Optional.of(joinRequests));
        // when
        String result = joinRequestsService.acceptJoinRequest(joinRequests.getId(), false);
        //then
        verify(joinRequestsRepository, times(1))
                .findById(joinRequests.getId());
        verify(joinRequestsRepository, times(1))
                .delete(joinRequests);
        assertThat(result).isEqualTo("거절되었습니다");
    }

    @Test
    @DisplayName("입장_신청_승인시_신청사용자_확인_불가로_IAE발생")
    void acceptJoinRequest03() throws Exception {
        // given

        // mocking
        when(joinRequestsRepository.findById(joinRequests.getId())).thenReturn(Optional.of(joinRequests));
        when(userRepository.findById(joinRequests.getUserId())).thenReturn(Optional.empty());
        // when

        //then
        assertThrows(IllegalArgumentException.class,
                () -> joinRequestsService.acceptJoinRequest(joinRequests.getId(), true),"존재하지 않는 유저입니다.");
        verify(joinRequestsRepository, times(1))
                .findById(joinRequests.getId());
        verify(joinRequestsRepository, never()).delete(joinRequests);
    }

    @Test
    @DisplayName("입장_신청_승인_중복")
    void acceptJoinRequest04() throws Exception {
        // given

        List<AllChatInfo> allChatInfos = new ArrayList<>();
        AllChatInfo allChatInfo = new AllChatInfo(userDetails02.getUser(), chatRoom);
        allChatInfos.add(allChatInfo);
        // mocking
        when(joinRequestsRepository.findById(joinRequests.getId())).thenReturn(Optional.of(joinRequests));
        when(userRepository.findById(joinRequests.getUserId())).thenReturn(Optional.of(userDetails02.getUser()));
        when(allChatInfoQueryRepository.findAllByUserIdOrderByIdDesc(userDetails02.getUser().getId()))
                .thenReturn(allChatInfos);
        // when
        String result = joinRequestsService.acceptJoinRequest(joinRequests.getId(), true);
        //then
        verify(joinRequestsRepository, times(1))
                .findById(joinRequests.getId());
        verify(joinRequestsRepository, never()).delete(joinRequests);
        verify(userRepository, times(1))
                .findById(joinRequests.getUserId());
        verify(allChatInfoQueryRepository, times(1))
                .findAllByUserIdOrderByIdDesc(userDetails02.getUser().getId());
        assertThat(result).isEqualTo("승인되었습니다");
    }

    @Test
    @DisplayName("입장_신청_승인")
    void acceptJoinRequest05() throws Exception {
        // given
        Location location02 = new Location("부산시 사하구", 37.222222, 126.222222);
        Menu menu = new Menu("분식", 1);
        Post newPost = new Post();
        ChatRoom newChatRoom = new ChatRoom(222L, "UUID", userDetails02.getUser().getId(), true, newPost);
        newPost = new Post(200L, "title", 3, "restaurant01", "2021-09-01 00:00:00",
                "contents", true, false, newChatRoom, userDetails02.getUser(), menu, location02,
                2.00, 1L, Post.meetingType.SEPARATE);
        newChatRoom = new ChatRoom(222L, "UUID", userDetails02.getUser().getId(), true, newPost);

        List<AllChatInfo> allChatInfos = new ArrayList<>();
        AllChatInfo allChatInfo = new AllChatInfo(userDetails02.getUser(), newChatRoom);
        allChatInfos.add(allChatInfo);
        // mocking
        when(joinRequestsRepository.findById(joinRequests.getId())).thenReturn(Optional.of(joinRequests));
        when(userRepository.findById(joinRequests.getUserId())).thenReturn(Optional.of(userDetails02.getUser()));
        when(allChatInfoQueryRepository.findAllByUserIdOrderByIdDesc(userDetails02.getUser().getId()))
                .thenReturn(allChatInfos);
        when(postQueryRepository.findById(post.getId())).thenReturn(post);
        when(allChatInfoQueryRepository.countAllByChatRoom(post.getChatRoom())).thenReturn(1L);
        when(chatRoomRepository.findByPostId(post.getId())).thenReturn(chatRoom);
        // when
        String result = joinRequestsService.acceptJoinRequest(joinRequests.getId(), true);
        //then
        verify(joinRequestsRepository, times(1)).findById(joinRequests.getId());
        verify(postQueryRepository, times(1)).findById(post.getId());
        verify(allChatInfoQueryRepository, times(1)).countAllByChatRoom(post.getChatRoom());
        verify(chatRoomRepository, times(1)).findByPostId(post.getId());
        assertThat(post.getNowHeadCount()).isEqualTo(2L);
        assertThat(result).isEqualTo("승인되었습니다");
    }

    @Test
    @DisplayName("채팅방_인원_초과로인한_입장_실패")
    void acceptJoinRequest06() throws Exception {
        // given
        Location location02 = new Location("부산시 사하구", 37.222222, 126.222222);
        Menu menu = new Menu("분식", 1);
        Post newPost = new Post();
        ChatRoom newChatRoom = new ChatRoom(222L, "UUID", userDetails02.getUser().getId(), true, newPost);
        newPost = new Post(200L, "title", 3, "restaurant01", "2021-09-01 00:00:00",
                "contents", true, false, newChatRoom, userDetails02.getUser(), menu, location02,
                2.00, 1L, Post.meetingType.SEPARATE);
        newChatRoom = new ChatRoom(222L, "UUID", userDetails02.getUser().getId(), true, newPost);

        List<AllChatInfo> allChatInfos = new ArrayList<>();
        AllChatInfo allChatInfo = new AllChatInfo(userDetails02.getUser(), newChatRoom);
        allChatInfos.add(allChatInfo);
        // mocking
        when(joinRequestsRepository.findById(joinRequests.getId())).thenReturn(Optional.of(joinRequests));
        when(userRepository.findById(joinRequests.getUserId())).thenReturn(Optional.of(userDetails02.getUser()));
        when(allChatInfoQueryRepository.findAllByUserIdOrderByIdDesc(userDetails02.getUser().getId()))
                .thenReturn(allChatInfos);
        when(postQueryRepository.findById(post.getId())).thenReturn(post);
        when(allChatInfoQueryRepository.countAllByChatRoom(post.getChatRoom())).thenReturn(4L);
        // when
//        String result = joinRequestsService.acceptJoinRequest(joinRequests.getId(), true);
        //then
        assertThrows(IllegalArgumentException.class,
                () -> joinRequestsService.acceptJoinRequest(joinRequests.getId(), true),
                "채팅방 인원 초과");
        verify(joinRequestsRepository, times(1)).findById(joinRequests.getId());
        verify(joinRequestsRepository, never()).delete(joinRequests);
        verify(postQueryRepository, times(1)).findById(post.getId());
        verify(allChatInfoQueryRepository, times(1)).countAllByChatRoom(post.getChatRoom());
        verify(chatRoomRepository, never()).findByPostId(post.getId());
        assertThat(post.getNowHeadCount()).isEqualTo(1L);


    }

    @Test
    @DisplayName("채팅방_인원수_제한")
    void checkHeadCount01() throws Exception {
        // given

        // mocking
        when(postQueryRepository.findById(post.getId())).thenReturn(post);
        when(allChatInfoQueryRepository.countAllByChatRoom(post.getChatRoom())).thenReturn(1L);
        // when
        Boolean result = joinRequestsService.checkHeadCount(post.getId());
        //then
        verify(postQueryRepository, times(1))
                .findById(post.getId());
        verify(allChatInfoQueryRepository, times(1)).countAllByChatRoom(post.getChatRoom());
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("채팅방_인원수_제한_실패")
    void checkHeadCount02() throws Exception {
        // given

        // mocking
        when(postQueryRepository.findById(post.getId())).thenReturn(post);
        when(allChatInfoQueryRepository.countAllByChatRoom(post.getChatRoom())).thenReturn(3L);
        // when
        Boolean result = joinRequestsService.checkHeadCount(post.getId());
        //then
        verify(postQueryRepository, times(1))
                .findById(post.getId());
        verify(allChatInfoQueryRepository, times(1)).countAllByChatRoom(post.getChatRoom());
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("테이블_중복_생성_불가")
    void checkDuplicate01() throws Exception {
        // given
        List<AllChatInfo> allChatInfos = new ArrayList<>();
        AllChatInfo allChatInfo = new AllChatInfo(userDetails02.getUser(), chatRoom);
        allChatInfos.add(allChatInfo);
        // mocking
        when(allChatInfoQueryRepository.findAllByUserIdOrderByIdDesc(userDetails02.getUser().getId()))
                .thenReturn(allChatInfos);
        // when
        Boolean result = joinRequestsService.checkDuplicate(userDetails02.getUser(), post.getId());
        //then
        verify(allChatInfoQueryRepository, times(1))
                .findAllByUserIdOrderByIdDesc(userDetails02.getUser().getId());
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("입장신청취소")
    void requestJoinCancel01() throws Exception {
        // given

        // mocking
        when(joinRequestsRepository.findByIdAndUserId(joinRequests.getId(), userDetails02.getUser().getId()))
                .thenReturn(Optional.of(joinRequests));
        // when
        joinRequestsService.requestJoinCancel(userDetails02, joinRequests.getId());
        //then
        verify(joinRequestsRepository, times(1))
                .findByIdAndUserId(joinRequests.getId(), userDetails02.getUser().getId());
        verify(joinRequestsRepository, times(1))
                .delete(joinRequests);
    }

    @Test
    @DisplayName("입장신청취소시_요청을_찾지못할때_IAE발생")
    void requestJoinCancel02() throws Exception {
        // given

        // mocking
        when(joinRequestsRepository.findByIdAndUserId(joinRequests.getId(), userDetails02.getUser().getId()))
                .thenReturn(Optional.empty());
        // when
        //then
        assertThrows(IllegalArgumentException.class,
                () -> joinRequestsService.requestJoinCancel(userDetails02, joinRequests.getId())
                , "해당 신청이 존재하지 않습니다.");
        verify(joinRequestsRepository, never()).delete(joinRequests);
    }

    @Test
    @DisplayName("게시글_삭제시_승인대기목록_삭제")
    void deleteByPostId01() throws Exception {
        // given

        // mocking

        // when
        joinRequestsService.deleteByPostId(joinRequests.getId());
        //then
        verify(joinRequestsRepository, times(1))
                .deleteByPostId(joinRequests.getId());
    }

    @Test
    @DisplayName("postId로_post찾아올때_null이면_IAE발생")
    void getPost01() throws Exception {
        // given

        // mocking
        when(postQueryRepository.findById(post.getId())).thenReturn(null);
        // when
        //then
        assertThrows(IllegalArgumentException.class,
                () -> joinRequestsService.getPost(post.getId()), "존재하지 않는 게시글입니다.");

    }

    @Test
    @DisplayName("postId로_post찾기_성공")
    void getPost02() throws Exception {
        // given

        // mocking
        when(postQueryRepository.findById(post.getId())).thenReturn(post);
        // when
        Post result = joinRequestsService.getPost(this.post.getId());
        //then
        verify(postQueryRepository, times(1))
                .findById(post.getId());
        assertThat(result.getId()).isEqualTo(post.getId());
        assertThat(result.getTitle()).isEqualTo(post.getTitle());
        assertThat(result.getHeadCount()).isEqualTo(post.getHeadCount());
        assertThat(result.getRestaurant()).isEqualTo(post.getRestaurant());
        assertThat(result.getOrderTime()).isEqualTo(post.getOrderTime());
        assertThat(result.getContents()).isEqualTo(post.getContents());
        assertThat(result.isCheckValid()).isEqualTo(post.isCheckValid());
        assertThat(result.isCheckDeleted()).isEqualTo(post.isCheckDeleted());
        assertThat(result.getChatRoom()).isEqualTo(post.getChatRoom());
        assertThat(result.getUser()).isEqualTo(post.getUser());
        assertThat(result.getMenu()).isEqualTo(post.getMenu());
        assertThat(result.getLocation()).isEqualTo(post.getLocation());
        assertThat(result.getDistance()).isEqualTo(post.getDistance());
        assertThat(result.getNowHeadCount()).isEqualTo(post.getNowHeadCount());
        assertThat(result.getMeetingType()).isEqualTo(post.getMeetingType());

    }

}