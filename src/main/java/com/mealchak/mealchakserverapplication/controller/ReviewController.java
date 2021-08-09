package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.dto.request.ReviewRequestDto;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.mapping.ReviewListMapping;
import com.mealchak.mealchakserverapplication.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"6. 리뷰작성"}) // Swagger
@RequiredArgsConstructor
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    // 리뷰 조회
    @ApiOperation(value = "리뷰 조회", notes = "리뷰를 조회합니다.")
    @GetMapping("/review")
    public List<ReviewListMapping> getReview(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reviewService.getReview(userDetails);
    }

    // 리뷰 생성
    @ApiOperation(value = "리뷰 작성", notes = "리뷰를 작성합니다.")
    @PostMapping("/review/{userId}")
    public void createReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                             @RequestBody ReviewRequestDto requestDto,
                             @PathVariable Long userId) {
        reviewService.createReview(userDetails, requestDto, userId);
    }

    // 리뷰 수정
    @ApiOperation(value = "리뷰 수정", notes = "리뷰를 수정합니다.")
    @PutMapping("/review/{reviewId}")
    public void updateReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                             @RequestBody ReviewRequestDto requestDto,
                             @PathVariable Long reviewId) {
        reviewService.updateReview(userDetails, requestDto, reviewId);
    }

    // 리뷰 삭제
    @ApiOperation(value = "리뷰 삭제", notes = "리뷰를 삭제제합니다")
    @DeleteMapping("/review/{reviewId}")
    public void deleteReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                             @PathVariable Long reviewId) {
        reviewService.deleteReview(userDetails, reviewId);
    }
}
