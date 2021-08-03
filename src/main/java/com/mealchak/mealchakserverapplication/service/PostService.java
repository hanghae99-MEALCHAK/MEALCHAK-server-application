package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.PostResponseDto;
import com.mealchak.mealchakserverapplication.dto.response.UserInfoAndPostResponseDto;
import com.mealchak.mealchakserverapplication.model.*;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.*;
import com.mealchak.mealchakserverapplication.repository.mapping.UserInfoMapping;
import com.sun.corba.se.spi.copyobject.CopyobjectDefaults;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;
    private final UserRoomRepository userRoomRepository;
    private final JoinRequestsRepository joinRequestsRepository;
    private final ChatRoomRepository chatRoomRepository;


    // 모집글 생성
    @Transactional
    public Long createPost(UserDetailsImpl userDetails, PostRequestDto requestDto, ChatRoom chatRoom) {
        if (userDetails != null) {
            User user = userDetails.getUser();
            Optional<Menu> menu = menuRepository.findByCategory(requestDto.getCategory());
            Long id;
            if (!menu.isPresent()) {
                Menu newMenu = new Menu(requestDto.getCategory(), 1);
                menuRepository.save(newMenu);
                Location location = new Location(requestDto);
                Post post = new Post(requestDto, user, newMenu, location, chatRoom);
                postRepository.save(post);
                id = post.getId();
            } else {
                menu.get().updateMenuCount(+1);
                Location location = new Location(requestDto);
                Post post = new Post(requestDto, user, menu.get(), location, chatRoom);
                postRepository.save(post);
                id = post.getId();
            }
            return id;
        } else {
            throw new IllegalArgumentException("로그인하지 않았습니다.");
        }
    }

    // 모집글 전체 조회
    public List<PostResponseDto> getAllPost() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtAsc();
        List<PostResponseDto> listPost = new ArrayList<>();
        for (Post post : posts) {
            Long nowHeadCount = userRoomRepository.countAllByChatRoom(post.getChatRoom());
            post.updateNowHeadCount(nowHeadCount);
            listPost.add(new PostResponseDto(post));
        }
        return listPost;
    }

    // 모집글 상세 조회
    public PostResponseDto getPostDetail(Long postId) {
        return new PostResponseDto(getPost(postId));
    }


    // fintById(postId)
    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("postId가 존재하지 않습니다."));
    }

    // 모집글 수정
    @Transactional
    public PostResponseDto updatePostDetail(Long postId, PostRequestDto requestDto) {
        Location location = new Location(requestDto);
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("postId가 존재하지 않습니다."));
        Menu menu = post.getMenu();
        if (requestDto.getCategory() != menu.getCategory()) {
            post.getMenu().updateMenuCount(-1);
            menu = menuRepository.findByCategory(requestDto.getCategory()).orElseThrow(() -> new IllegalArgumentException("메뉴가 존재하지 않습니다"));
            menu.updateMenuCount(+1);
        }
        post.update(requestDto, menu, location);
        return new PostResponseDto(post);
    }

    // 모집글 삭제
    @Transactional
    public void deletePost(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("postId가 존재하지 않습니다."));
        if (!post.getChatRoom().getOwnUserId().equals(user.getId())) {
            throw new IllegalArgumentException(("삭제 권한이 없습니다."));
        } else {
            chatRoomService.deletePost(postId);
            post.getMenu().updateMenuCount(-1);
            postRepository.deleteById(postId);
        }
    }

    // 모집글 검색
    public List<Post> getSearch(String text) {
        return postRepository.findByTitleContainingOrContentsContainingOrderByCreatedAtDesc(text, text);
    }

    // 모집글 유저 위치 기반 조회
    public Collection<PostResponseDto> getPostByUserDist(UserDetailsImpl userDetails, int range, int max) {
        if (userDetails == null) {
            return getAllPost();
        }
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다."));

        String[] userGuName = user.getLocation().getAddress().split(" ");
        String guName = userGuName[1];
        if (max == 1) {
            guName = userGuName[0];
        }
        List<Post> postList = postRepository.findByLocationAddressContainingIgnoreCase(guName);
        Map<Double, PostResponseDto> nearPost = new TreeMap<>();
        List<Double> distChecker = new ArrayList<>();
        for (Post post : postList) {
            double lat1 = user.getLocation().getLatitude();
            double lon1 = user.getLocation().getLongitude();
            double lat2 = post.getLocation().getLatitude();
            double lon2 = post.getLocation().getLongitude();

            double theta = lon1 - lon2;
            double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                    * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            dist = Math.round(dist * 1000) / 1000.0;

            if (dist < range) {
                post.updateDistance(dist);
                PostResponseDto responsDto = new PostResponseDto(post);
                if (distChecker.contains(dist)) {
                    for (int i = 0; i < 100; i++) {
                        dist += 0.001;
                        if (!distChecker.contains(dist)) {
                            distChecker.add(dist);
                            nearPost.put(dist, responsDto);
                            break;
                        }
                    }
                } else {
                    distChecker.add(dist);
                    nearPost.put(dist, responsDto);
                }
            }
        }
        return nearPost.values();
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    //유저 신청정보 저장
    public String requestJoin(UserDetailsImpl userDetails, Long id) {
        Long userId = userDetails.getUser().getId();
        if (joinRequestsRepository.findByUserIdAndPostId(userId, id) == null) {
            User user = postRepository.findById(id).get().getUser();
            Long ownUserId = user.getId();
            JoinRequests joinRequests = new JoinRequests(userId, id, ownUserId);
            joinRequestsRepository.save(joinRequests);
            return "신청완료";
        } else {
            return "이미 신청한 글입니다";
        }
    }

    //유저 신청정보 불러오기
    public List<UserInfoAndPostResponseDto> requestJoinList(UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();

        List<JoinRequests> joinRequestsList = joinRequestsRepository.findByOwnUserId(userId);

        List<UserInfoAndPostResponseDto> userInfoAndPostResponseDtoList = new ArrayList<>();

        for (JoinRequests joinRequests : joinRequestsList) {

            UserInfoMapping userInfoMapping = userRepository.findById(joinRequests.getUserId(), UserInfoMapping.class).orElseThrow(
                    () -> new IllegalArgumentException("회원이 아닙니다.")
            );

            Post post = postRepository.findById(joinRequests.getPostId()).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 게시글입니다.")
            );

            UserInfoAndPostResponseDto userInfoAndPostResponseDto = new UserInfoAndPostResponseDto();

            userInfoAndPostResponseDto.setUserId(userInfoMapping.getId());
            userInfoAndPostResponseDto.setUsername(userInfoMapping.getUsername());
            userInfoAndPostResponseDto.setProfileImg(userInfoMapping.getProfileImg());
            userInfoAndPostResponseDto.setPostTitle(post.getTitle());

            userInfoAndPostResponseDtoList.add(userInfoAndPostResponseDto);
        }

        return userInfoAndPostResponseDtoList;

    }

    public String acceptJoinRequest(Long joinRequestId, boolean tOrF) {
        JoinRequests joinRequests = joinRequestsRepository.findById(joinRequestId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 신청입니다.")
        );
        if (!tOrF) {
            joinRequestsRepository.delete(joinRequests);
            return "거절되었습니다";
        }
        User user = userRepository.findById(joinRequests.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 유저입니다.")
        );
        Long postId = joinRequests.getPostId();
        if (!chatRoomService.checkDuplicate(user, postId)) {
            if (chatRoomService.checkHeadCount(postId)) {
                ChatRoom chatRoom = chatRoomRepository.findByPostId(postId);
                AllChatInfo allChatInfo = new AllChatInfo(user, chatRoom);
                userRoomRepository.save(allChatInfo);
                joinRequestsRepository.delete(joinRequests);
            } else {
                throw new IllegalArgumentException("채팅방 인원 초과");
            }
        }
        return "승인되었습니다";
    }
}