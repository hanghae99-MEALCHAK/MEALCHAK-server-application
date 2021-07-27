package com.mealchak.mealchakserverapplication.service;

import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import com.mealchak.mealchakserverapplication.model.Post;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    // 모집글 생성
    @Transactional
    public void createPost(User user, PostRequestDto requestDto) {
        Post post = new Post(user.getUsername(), requestDto);
        postRepository.save(post);
    }

    // 모집글 전체 조회
    public List<Post> getAllPost() {
        return postRepository.findAll();
    }

    // 모집글 상세 조회
    public Post getPostDetail(Long postId) {
        return getPost(postId);
    }

    // 모집글 수정
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
}