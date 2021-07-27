package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import com.mealchak.mealchakserverapplication.model.Post;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"1. 모집글"}) // Swagger # 1. 모집글
@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;

    // 모집글 생성
    @ApiOperation(value = "모집글 작성", notes = "전체 모집글 조회합니다.")
    @PostMapping("/posts")
    public void createPost(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody PostRequestDto requestDto) {
        if (userDetails != null) {
            postService.createPost(userDetails.getUser(), requestDto);
        } else {
            throw new IllegalArgumentException("로그인 하지 않았습니다.");
        }
    }

    // 모집글 불러오기
    @ApiOperation(value = "전체 모집글 조회", notes = "전체 모집글 조회합니다.")
    @GetMapping("/posts")
    public List<Post> getAllPost() {
        return postService.getAllPost();
    }

    // 특정 모집글 불러오기
    @ApiOperation(value = "전체 모집글 조회", notes = "전체 모집글 조회합니다.")
    @GetMapping("/posts/{postId}")
    public Post getPostDetail(@PathVariable Long postId) {
        return postService.getPostDetail(postId);
    }

    // 검색하여 모집글 불러오기
    @ApiOperation(value = "모집글 검색 조회", notes = "모집글을 검색 조회 합니다.")
    @PostMapping("/posts")
    public List<Post> getSearch(@RequestBody String text) {
        return postService.getSearch(text);
    }

    // 특정 모집글 수정
    @ApiOperation(value = "전체 모집글 조회", notes = "전체 모집글 조회합니다.")
    @PutMapping("/posts/{postId}")
    public Post updatePostDetail(@PathVariable Long postId, @RequestBody PostRequestDto requestDto) {
        return postService.updatePostDetail(postId, requestDto);
    }

    // 특정 모집글 삭제
    @ApiOperation(value = "전체 모집글 조회", notes = "전체 모집글 조회합니다.")
    @DeleteMapping("/posts/{postId}")
    public void getPostDelete(@PathVariable Long postId) {
        postService.deletePost(postId);
    }
}