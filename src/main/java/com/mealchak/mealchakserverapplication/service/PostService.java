package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.PostDetailResponseDto;
import com.mealchak.mealchakserverapplication.dto.response.PostResponseDto;
import com.mealchak.mealchakserverapplication.model.*;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.AllChatInfoRepository;
import com.mealchak.mealchakserverapplication.repository.MenuRepository;
import com.mealchak.mealchakserverapplication.repository.PostRepository;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
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
    private final AllChatInfoRepository allChatInfoRepository;
    private final AllChatInfoService allChatInfoService;
    private final JoinRequestsService joinRequestsService;
    private static final int RANGE = 3;

    // 모집글 생성
    @Transactional
    public void createPost(UserDetailsImpl userDetails, PostRequestDto requestDto, ChatRoom chatRoom) {
        if (userDetails != null) {
            User user = userDetails.getUser();
            Optional<Menu> menu = menuRepository.findByCategory(requestDto.getCategory());
            if (!menu.isPresent()) {
                Menu newMenu = new Menu(requestDto.getCategory(), 1);
                menuRepository.save(newMenu);
                Location location = new Location(requestDto);
                Post post = new Post(requestDto, user, newMenu, location, chatRoom);
                postRepository.save(post);
            } else {
                menu.get().updateMenuCount(+1);
                Location location = new Location(requestDto);
                Post post = new Post(requestDto, user, menu.get(), location, chatRoom);
                postRepository.save(post);
            }
        } else {
            throw new IllegalArgumentException("로그인하지 않았습니다.");
        }
    }

    // 모집글 전체 조회
    public List<PostResponseDto> getAllPost(String category) {
        List<PostResponseDto> listPost = new ArrayList<>();
        List<Post> posts = postRepository.findByCheckValidTrueAndMenu_CategoryContainingOrderByOrderTimeAsc(category);

        for (Post post : posts) {
            updateHeadCount(post);
            listPost.add(new PostResponseDto(post));
        }
        return listPost;
    }

    // 모집글 상세 조회
    public PostDetailResponseDto getPostDetail(Long postId) {
        Post post = getPost(postId);
        List<User> userList = allChatInfoService.getUser(post.getChatRoom().getId());
        updateHeadCount(post);
        return new PostDetailResponseDto(post, userList);
    }

    // 내가 쓴 글 조회
    public List<PostResponseDto> getMyPost(UserDetailsImpl userDetails) {
        if (userDetails != null) {
            User user = userDetails.getUser();
            List<Post> posts = postRepository.findByCheckDeletedFalseAndUser_IdOrderByCreatedAtDesc(user.getId());
            List<PostResponseDto> listPost = new ArrayList<>();
            for (Post post : posts) {
                updateHeadCount(post);
                listPost.add(new PostResponseDto(post));
            }
            return listPost;
        } else {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
    }

    // 모집글 수정
    @Transactional
    public PostResponseDto updatePostDetail(Long postId, PostRequestDto requestDto, UserDetailsImpl userDetails) {
        Location location = new Location(requestDto);
        Post post = postRepository.findByCheckValidTrueAndIdAndUserId(postId, userDetails.getUser().getId()).orElseThrow(
                () -> new IllegalArgumentException("postId가 존재하지 않거나 만료된 post 입니다."));
        Menu menu = post.getMenu();
        if (!requestDto.getCategory().equals(menu.getCategory())) {
            post.getMenu().updateMenuCount(-1);
            menu = menuRepository.findByCategory(requestDto.getCategory()).orElseThrow(
                    () -> new IllegalArgumentException("메뉴가 존재하지 않습니다"));
            menu.updateMenuCount(+1);
        }
        updateHeadCount(post);
        post.update(requestDto, menu, location);
        return new PostResponseDto(post);
    }

    // 모집글 삭제
    @Transactional
    public void deletePost(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("postId가 존재하지 않습니다."));
        if (post.getChatRoom().getOwnUserId().equals(userDetails.getUser().getId())) {
            Long chatRoomId = post.getChatRoom().getId();
            post.getMenu().updateMenuCount(-1);
            post.deleted(true);
            post.expired(false);
            joinRequestsService.deleteByPostId(post.getId());
            chatRoomService.deleteAllChatInfo(chatRoomId, userDetails);
            chatRoomService.updateChatValid(chatRoomId);
        } else {
            throw new IllegalArgumentException(("삭제 권한이 없습니다."));
        }
    }

    // 검색하여 모집글 불러오기
    public List<PostResponseDto> getSearch(UserDetailsImpl userDetails, String text, String sort) {
        if (userDetails == null) {
            return getSearchPost(text);
        } else {
            User user = userDetails.getUser();
            if (sort.equals("recent")) {
                return getSearchPostBySortByRecent(text, user);
            } else if (sort.equals("nearBy")) {
                return getSearchPostByUserDist(text, user);
            } else {
                throw new IllegalArgumentException("잘못된 sort 요청입니다.");
            }
        }
    }

    // 유저 근처에 작성된 게시글 조회
    public List<PostResponseDto> getPostByUserDist(UserDetailsImpl userDetails, String category, String sort) {
        // 게스트 유저일 경우 모든 결과 조회
        if (userDetails == null) {
            return getAllPost(category);
        }
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다."));

        double userLatitude = user.getLocation().getLatitude();
        double userLongitude = user.getLocation().getLongitude();
        List<Post> postList;
        if (sort.equals("recent")) {
            return getAllPostSortByRecent(user, userLatitude, userLongitude, category);
        } else if (sort.equals("nearBy")) {
            postList = getPostByCategory(category, userLatitude, userLongitude);
        } else {
            throw new IllegalArgumentException("잘못된 sort 요청입니다.");
        }
        return getNearByResult(postList, user);
    }

    // 비회원 검색
    public List<PostResponseDto> getSearchPost(String text) {
        List<Post> posts =
                postRepository.findByCheckValidTrueAndTitleContainingOrCheckValidTrueAndContentsContainingOrCheckValidTrueAndLocation_AddressContainingOrderByOrderTimeAsc(
                        text, text, text);
        List<PostResponseDto> listPost = new ArrayList<>();
        for (Post post : posts) {
            updateHeadCount(post);
            listPost.add(new PostResponseDto(post));
        }
        return listPost;
    }

    // 회원 검색 (모집 마감임박순)
    public List<PostResponseDto> getSearchPostBySortByRecent(String text, User user) {
        List<Post> posts =
                postRepository.findByCheckValidTrueAndTitleContainingOrCheckValidTrueAndContentsContainingOrCheckValidTrueAndLocation_AddressContainingOrderByOrderTimeAsc(
                        text, text, text);
        return getPostsDistance(user, posts);
    }

    // 회원 검색 (거리순)
    public List<PostResponseDto> getSearchPostByUserDist(String text, User user) {
        List<Post> posts = postRepository.findByCheckValidTrueAndTitleContainingOrCheckValidTrueAndContentsContainingOrCheckValidTrueAndLocation_AddressContaining(
                text, text, text);
        List<Post> listPost = new ArrayList<>();
        for (Post post : posts) {
            updateHeadCount(post);
            double dist = getDist(user, post);
            if (dist < RANGE) {
                post.updateDistance(dist);
                listPost.add(post);
            }
        }
        return getNearByResult(listPost, user);
    }

    // 거리순 모집글 결과 리스트
    public List<PostResponseDto> getNearByResult(List<Post> postList, User user) {
        Map<Double, PostResponseDto> nearPost = new TreeMap<>();
        List<Double> distChecker = new ArrayList<>();
        for (Post post : postList) {
            double dist = getDist(user, post);
            updateHeadCount(post);
            if (dist < RANGE) {
                post.updateDistance(dist);
                PostResponseDto responseDto = new PostResponseDto(post);
                if (distChecker.contains(dist)) {
                    for (int i = 0; i < 100; i++) {
                        dist += 0.001;
                        if (!distChecker.contains(dist)) {
                            distChecker.add(dist);
                            nearPost.put(dist, responseDto);
                            break;
                        }
                    }
                } else {
                    distChecker.add(dist);
                    nearPost.put(dist, responseDto);
                }
            }
        }
        return new ArrayList<>(nearPost.values());
    }

    // 모집글 전체 최신순 조회
    public List<PostResponseDto> getAllPostSortByRecent(User user, double userLatitude, double userLongitude, String category) {
        List<Post> posts = getPostByCategory(category, userLatitude, userLongitude);
        return getPostsDistance(user, posts);
    }

    // 모집글에 거리 표시
    private List<PostResponseDto> getPostsDistance(User user, List<Post> posts) {
        List<PostResponseDto> listPost = new ArrayList<>();
        for (Post post : posts) {
            updateHeadCount(post);
            double dist = getDist(user, post);
            if (dist < RANGE) {
                post.updateDistance(dist);
                listPost.add(new PostResponseDto(post));
            }
        }
        return listPost;
    }

    // 카테고리별 리스트 조회
    public List<Post> getPostByCategory(String category, double userLatitude, double userLongitude) {
        if (category.equals("전체")) {
            return postRepository.findByCheckValidTrueAndLocation_LatitudeBetweenAndLocation_LongitudeBetweenOrderByOrderTimeAsc(
                    userLatitude - 0.027,
                    userLatitude + 0.027,
                    userLongitude - 0.036,
                    userLongitude + 0.036
            );
        } else {
            return postRepository.findByCheckValidTrueAndLocation_LatitudeBetweenAndLocation_LongitudeBetweenAndMenu_CategoryContainingOrderByOrderTimeAsc(
                    userLatitude - 0.027,
                    userLatitude + 0.027,
                    userLongitude - 0.036,
                    userLongitude + 0.036,
                    category
            );
        }
    }

    // 유저와 게시글의 거리계산 로직
    private static double getDist(User user, Post post) {
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
        return dist;
    }

    private static double deg2rad(double deg) {return (deg * Math.PI / 180.0);}
    private static double rad2deg(double rad) {return (rad * 180 / Math.PI);}

    // 모집글 HeadCount 추가
    public void updateHeadCount(Post post) {
        Long nowHeadCount = allChatInfoRepository.countAllByChatRoom(post.getChatRoom());
        post.updateNowHeadCount(nowHeadCount);
    }

    // findById(postId)
    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("postId가 존재하지 않습니다."));
    }
}