package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.PostResponseDto;
import com.mealchak.mealchakserverapplication.model.*;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.*;
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
    public List<PostResponseDto> getAllPost() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostResponseDto> listPost = new ArrayList<>();
        for (Post post : posts) {
            updateHeadCount(post);
            listPost.add(new PostResponseDto(post));
        }
        return listPost;
    }

    // 모집글 상세 조회
    public PostResponseDto getPostDetail(Long postId) {
        Post post = getPost(postId);
        updateHeadCount(post);
        return new PostResponseDto(post);
    }

    // 모집글 HeadCount 추가
    public void updateHeadCount(Post post) {
        Long nowHeadCount = allChatInfoRepository.countAllByChatRoom(post.getChatRoom());
        post.updateNowHeadCount(nowHeadCount);
    }

    // findById(postId)
    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("postId가 존재하지 않습니다."));
    }

    // 모집글 수정
    @Transactional
    public PostResponseDto updatePostDetail(Long postId, PostRequestDto requestDto) {
        Location location = new Location(requestDto);
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("postId가 존재하지 않습니다."));
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
    public void deletePost(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("postId가 존재하지 않습니다."));
        if (!post.getChatRoom().getOwnUserId().equals(user.getId())) {
            throw new IllegalArgumentException(("삭제 권한이 없습니다."));
        } else {
            chatRoomService.deletePost(postId);
            post.getMenu().updateMenuCount(-1);
            postRepository.deleteById(postId);
        }
    }

    // 모집글 검색
    public List<PostResponseDto> getSearch(String text) {
        List<Post> posts = postRepository.findByTitleContainingOrContentsContainingOrderByCreatedAtDesc(text, text);
        List<PostResponseDto> listPost = new ArrayList<>();
        for (Post post : posts) {
            updateHeadCount(post);
            listPost.add(new PostResponseDto(post));
        }
        return listPost;
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
            updateHeadCount(post);
            if (dist < range) {
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
        return nearPost.values();
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


}