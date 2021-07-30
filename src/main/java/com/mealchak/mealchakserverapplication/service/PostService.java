package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import com.mealchak.mealchakserverapplication.model.Location;
import com.mealchak.mealchakserverapplication.model.Post;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.repository.PostRepository;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 모집글 생성
    @Transactional
    public void createPost(User user, PostRequestDto requestDto) {
        Location location = new Location(requestDto);
        Post post = new Post(user.getUsername(), user.getId(), user.getThumbnailImg(), requestDto, location);
//        CategoryCounter categoryCounter = post.getCategoryCounter();



        postRepository.save(post);
    }

    // 모집글 전체 조회
    public List<Post> getAllPost() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    // 모집글 상세 조회
    public Post getPostDetail(Long postId) {
        return getPost(postId);
    }

    // 모집글 수정
    @Transactional
    public Post updatePostDetail(Long postId, PostRequestDto requestDto) {
        Post post = getPost(postId);
        post.update(requestDto);
        return post;
    }

    // 모집글 삭제
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("postId가 존재하지 않습니다."));
    }

    // 모집글 검색
    public List<Post> getSearch(String text) {
        return postRepository.findByTitleContainingOrContentsContainingOrCategoryContainingOrderByCreatedAtDesc(text, text, text);
    }

    // 모집글 유저 위치 기반 조회
    public Map<Double, Post> getPostByUserDist(Long id){
        User user = userRepository.findById(id).orElseThrow(
                ()-> new IllegalArgumentException("해당 아이디가 존재하지 않습니다."));
        List<Post> postList = postRepository.findAllByLocationAddressIgnoreCase(user.getLocation().getAddress());
        Map<Double, Post> nearPost = new TreeMap<>();
        for (Post posts : postList) {
            double lat1 = user.getLocation().getLatitude();
            double lon1 = user.getLocation().getLongitude();
            double lat2 = posts.getLocation().getLatitude();
            double lon2 = posts.getLocation().getLongitude();

            double theta = lon1 - lon2;
            double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                    * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;

            dist = dist * 1.609344;

            if (dist < 5) {
                posts.updateDistance(dist);
                nearPost.put(posts.getDistance(), posts);
            }
        }
        return nearPost;
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
