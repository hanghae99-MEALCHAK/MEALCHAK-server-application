package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.dto.request.PostRequestDto;
import com.mealchak.mealchakserverapplication.dto.response.PostDetailResponseDto;
import com.mealchak.mealchakserverapplication.dto.response.PostResponseDto;
import com.mealchak.mealchakserverapplication.model.ChatRoom;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.service.ChatRoomService;
import com.mealchak.mealchakserverapplication.service.PostService;
import com.mealchak.mealchakserverapplication.service.UserRoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"1. 모집글"}) // Swagger
@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;
    private final ChatRoomService chatRoomService;
    private final UserRoomService userRoomService;

    // 모집글 생성
    @ApiOperation(value = "모집글 작성", notes = "모집글 작성합니다.")
    @PostMapping("/posts")
    public void createPost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                           @RequestBody PostRequestDto requestDto) {
        ChatRoom chatRoom = chatRoomService.createChatRoom(userDetails.getUser());
        postService.createPost(userDetails, requestDto, chatRoom);
        userRoomService.save(userDetails.getUser(), chatRoom);
    }

    // 모집글 전체 불러오기
    @ApiOperation(value = "전체 모집글 조회", notes = "전체 모집글 조회합니다.")
    @GetMapping("/posts")
    public List<PostResponseDto> getAllPost(@RequestParam(value = "category", required = false, defaultValue = "전체") String category) {
        return postService.getAllPost(category);
    }

    // 해당 모집글 불러오기
    @ApiOperation(value = "해당 모집글 조회", notes = "해당 모집글 조회합니다.")
    @GetMapping("/posts/{postId}")
    public PostDetailResponseDto getPostDetail(@PathVariable Long postId) {
        return postService.getPostDetail(postId);
    }

    // 내가 쓴 모집글 불러오기
    @ApiOperation(value = "내가 쓴 모집글 불러오기", notes = "내가 쓴 모집글 조회합니다.")
    @GetMapping("http://localhost:8080/posts/myPosts")
    public List<PostResponseDto> getMyPost(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getMyPost(userDetails);
    }

    // 해당 모집글 수정
    @ApiOperation(value = "해당 모집글 수정", notes = "해당 모집글 수정합니다.")
    @PutMapping("/posts/{postId}")
    public PostResponseDto updatePostDetail(@PathVariable Long postId, @RequestBody PostRequestDto requestDto,  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.updatePostDetail(postId, requestDto, userDetails);
    }

    // 특정 모집글 삭제
    @ApiOperation(value = "해당 모집글 삭제", notes = "해당 모집글 삭제.")
    @DeleteMapping("/posts/{postId}")
    public void getPostDelete(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(postId, userDetails);
    }

    // 검색하여 모집글 불러오기
    @ApiOperation(value = "모집글 검색 조회", notes = "모집글을 검색 조회 합니다.")
    @GetMapping("/search")
    public List<PostResponseDto> getSearch(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestParam(value = "keyword") String keyword,
                                           @RequestParam(value = "sort", required = false, defaultValue = "recent") String sort) {
        return postService.getSearch(userDetails, keyword, sort);
    }

    // 유저 근처에 작성된 게시글 조회
    @ApiOperation(value = "위치 기반 모집글 조회", notes = "사용자 위치를 기반으로 모집글을 조회합니다.")
    @GetMapping("/posts/around")
    public List<PostResponseDto> getPostByUserDist(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @RequestParam(value = "category", required = false, defaultValue = "전체") String category,
                                                   @RequestParam(value = "sort", required = false, defaultValue = "recent") String sort) {
        return postService.getPostByUserDist(userDetails, category, sort);
    }

}