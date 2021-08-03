package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.PostResponseDto;
import com.mealchak.mealchakserverapplication.model.Location;
import com.mealchak.mealchakserverapplication.model.Menu;
import com.mealchak.mealchakserverapplication.model.Post;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
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

    // 모집글 생성
    @Transactional
    public Long createPost(UserDetailsImpl userDetails, PostRequestDto requestDto) {
        if (userDetails != null) {
            User user = userDetails.getUser();
            Optional<Menu> menu = menuRepository.findByCategory(requestDto.getCategory());
            Long id;
            if (!menu.isPresent()) {
                Menu newMenu = new Menu(requestDto.getCategory(), 1);
                menuRepository.save(newMenu);
                Location location = new Location(requestDto);
                Post post = new Post(requestDto, user, newMenu, location);
                postRepository.save(post);
                id = post.getId();
            } else {
                menu.get().updateMenuCount(+1);
                Location location = new Location(requestDto);
                Post post = new Post(requestDto, user, menu.get(), location);
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
        posts.forEach((post) -> listPost.add(new PostResponseDto(post)));
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
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("postId가 존재하지 않습니다."));
        post.getMenu().updateMenuCount(-1);
        postRepository.deleteById(postId);
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
        if (max == 1) {guName = userGuName[0];}
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
}