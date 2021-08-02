package com.mealchak.mealchakserverapplication.controller;


import com.mealchak.mealchakserverapplication.dto.request.SignupRequestDto;
import com.mealchak.mealchakserverapplication.dto.request.UserUpdateDto;
import com.mealchak.mealchakserverapplication.dto.response.HeaderDto;
import com.mealchak.mealchakserverapplication.jwt.JwtTokenProvider;
import com.mealchak.mealchakserverapplication.model.Location;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.UserInfoRepository;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
import com.mealchak.mealchakserverapplication.repository.mapping.UserInfoMapping;
import com.mealchak.mealchakserverapplication.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"2. User, kakao login"}) // Swagger
@RestController
@RequiredArgsConstructor
public class UserController {

    private final BCryptPasswordEncoder encodePassword;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository; // 테스트를위함, 나중에 서비스로 편입시킬것것
    private final UserInfoRepository userInfoRepository; // 테스트를위함, 나중에 서비스로 편입시킬것것


    @ApiOperation(value = "kakao소셜 로그인", notes = "kakao소셜 로그인.")
    //카카오 로그인 api로 코드를 받아옴
    @GetMapping("/user/kakao/callback")
    @ResponseBody
    public HeaderDto kakaoLogin(@RequestParam(value = "code") String code) {
        //서비스에 정의된 kakaoLogin이 email을 반환합니다
        String email = userService.kakaoLogin(code);
        //해당 이메일로 db에서 해당유저의 row를 가져옵니다
        User member = userService.getUser(email);
        //토큰을 담을 Dto 객체를 만들고
        HeaderDto headerDto = new HeaderDto();
        //해당 dto안에있는 TOKEN값에 jwt를 생성하여 담습니다
        //현재 jwt에 저장되는 정보는 username과 id(pk)입니다
        headerDto.setTOKEN(jwtTokenProvider.createToken(member.getEmail(), member.getId(), member.getUsername()));
        //dto를 반환합니다. 후에 프론트에서 해당 dto에 담긴 token을 A-AUTH-TOKEN 헤더에 담아 전달해줄겁니다
        return headerDto;
    }

    @ApiOperation(value = "유저 정보 조회", notes = "유저 정보 조회")
    @GetMapping("/user/info")
    public UserInfoMapping userinfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null) {
            return userInfoRepository.findByEmail(userDetails.getUser().getEmail()).orElseThrow(() -> new IllegalArgumentException("회원이 아닙니다."));
        } else {

            throw new IllegalArgumentException("로그인 하지 않았습니다.");
        }
    }

    @ApiOperation(value = "유저 닉네임 수정", notes = "유저 닉네임 수정")
    @PutMapping("/username/update")
    public String updateUsername(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody String newUsername) {
        if (userDetails != null) {
            return userService.updateUsername(userDetails.getUser(), newUsername);
        } else {
            throw new IllegalArgumentException("로그인 하지 않았습니다.");

    // 회원 가입 요청 처리
    @ApiOperation(value = "회원 가입 요청", notes = "회원 가입 요청합니다.")
    @PostMapping("/user/signup")
    public void registerUser(@Valid @RequestBody SignupRequestDto requestDto) {
        userService.registerUser(requestDto);
    }

    @ApiOperation(value = "로그인 요청", notes = "로그인 요청합니다.")
    @PostMapping("/user/login")
    public String login(@RequestBody SignupRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 유저입니다."));
        if (!encodePassword.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return jwtTokenProvider.createToken(user.getEmail(), user.getId(), user.getUsername());
    }

    // 유저 위치 저장 (위도, 경도, 주소)
    @ApiOperation(value = "유저 위치 저장", notes = "유저의 위치를 저장합니다.")
    @PutMapping("/user/location")
    public Location updateUserLocation(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserUpdateDto updateDto) {
        return userService.updateUserLocation(updateDto,userDetails.getUser());
    }
}
