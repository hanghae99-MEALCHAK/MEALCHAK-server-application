package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.dto.request.SignupRequestDto;
import com.mealchak.mealchakserverapplication.dto.request.UserInfoUpdateDto;
import com.mealchak.mealchakserverapplication.dto.request.UserLocationUpdateDto;
import com.mealchak.mealchakserverapplication.dto.response.HeaderDto;
import com.mealchak.mealchakserverapplication.dto.response.UserInfoResponseDto;
import com.mealchak.mealchakserverapplication.model.Location;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.mapping.UserInfoMapping;
import com.mealchak.mealchakserverapplication.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"2. User, kakao login"}) // Swagger
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //카카오 로그인 api로 코드를 받아옴
    @ApiOperation(value = "kakao소셜 로그인", notes = "kakao소셜 로그인.")
    @GetMapping("/user/kakao/callback")
    @ResponseBody
    public HeaderDto kakaoLogin(@RequestParam(value = "code") String code) {
        return userService.kakaoLogin(code);
    }

    @ApiOperation(value = "유저 정보 조회", notes = "유저 정보 조회")
    @GetMapping("/user/info")
    public UserInfoMapping userinfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.userInfo(userDetails);
    }

    @ApiOperation(value = "유저 정보 수정", notes = "유저 정보 수정")
    @PutMapping("/userInfo/update")
    public UserInfoResponseDto updateUsername(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserInfoUpdateDto updateDto) {
        return userService.updateUsername(userDetails, updateDto);
    }

    // 회원 가입 요청 처리
    @ApiOperation(value = "회원 가입 요청", notes = "회원 가입 요청합니다.")
    @PostMapping("/user/signup")
    public void registerUser(@Valid @RequestBody SignupRequestDto requestDto) {
        userService.registerUser(requestDto);
    }

    @ApiOperation(value = "로그인 요청", notes = "로그인 요청합니다.")
    @PostMapping("/user/login")
    public String login(@RequestBody SignupRequestDto requestDto) {
        return userService.login(requestDto);
    }

    // 유저 위치 저장 (위도, 경도, 주소)
    @ApiOperation(value = "유저 위치 저장", notes = "유저의 위치를 저장합니다.")
    @PutMapping("/user/location")
    public Location updateUserLocation(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserLocationUpdateDto updateDto) {
        return userService.updateUserLocation(updateDto, userDetails.getUser());
    }
}
