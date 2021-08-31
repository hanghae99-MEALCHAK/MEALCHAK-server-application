package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.dto.response.MyAwaitRequestJoinResponseDto;
import com.mealchak.mealchakserverapplication.dto.response.UserInfoAndPostResponseDto;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.service.JoinRequestsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"7. 게시글(채팅방)입장신청"}) // Swagger
@RestController
@RequiredArgsConstructor
public class JoinRequestController {

    private final JoinRequestsService joinRequestsService;

    @ApiOperation(value = "게시글 입장 신청", notes = "게시글 입장 신청")
    @GetMapping("/posts/join/request/{postId}")
    public String requestJoin(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long postId) {
        return joinRequestsService.requestJoin(userDetails, postId);
    }

    @ApiOperation(value = "게시글 입장 신청 취소", notes = "게시글 입장 신청 취소")
    @DeleteMapping("/posts/join/request/{id}")
    public void requestJoinCancel(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        joinRequestsService.requestJoinCancel(userDetails, id);
    }

    @ApiOperation(value = "게시글 입장 신청 목록", notes = "게시글 입장 신청 목록")
    @GetMapping("/posts/join/request/list")
    public List<UserInfoAndPostResponseDto> requestJoinList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return joinRequestsService.requestJoinList(userDetails);
    }

    @ApiOperation(value = "게시글 입장 신청 승인/비승인", notes = "게시글 입장 신청 승인/비승인")
    @GetMapping("/posts/join/request/accept/{joinRequestId}")
    public String acceptJoinRequest(@PathVariable Long joinRequestId, @RequestParam(value = "accept") boolean tOrF) {
        return joinRequestsService.acceptJoinRequest(joinRequestId, tOrF);
    }

    @ApiOperation(value = "나의 게시글 입장 신청 대기 목록", notes = "나의 게시글 입장 신청 대기 목록")
    @GetMapping("/posts/join/request/await")
    public List<MyAwaitRequestJoinResponseDto> myAwaitRequestJoinList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return joinRequestsService.myAwaitRequestJoinList(userDetails);
    }
}
