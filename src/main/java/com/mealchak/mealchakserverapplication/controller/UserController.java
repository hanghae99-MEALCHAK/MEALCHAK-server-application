package com.mealchak.mealchakserverapplication.controller;

import com.mealchak.mealchakserverapplication.config.WebSecurityConfig;
import com.mealchak.mealchakserverapplication.dto.HeaderDto;
import com.mealchak.mealchakserverapplication.dto.UserInfoDto;
import com.mealchak.mealchakserverapplication.dto.request.SignupRequestDto;
import com.mealchak.mealchakserverapplication.jwt.JwtTokenProvider;
import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.oauth2.UserDetailsImpl;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
import com.mealchak.mealchakserverapplication.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final BCryptPasswordEncoder encodePassword;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository; // 테스트를위함, 나중에 서비스로 편입시킬것것


    //카카오 로그인 api로 코드를 받아옴
    @GetMapping("/user/kakao/callback")
    @ResponseBody
    public HeaderDto kakaoLogin(@RequestParam(value = "code") String code) {
        //서비스에 정의된 kakaoLogin이 email을 반환합니다
        String email = userService.kakaoLogin(code);
        //해당 이메일로 db에서 해당유저의 row를 가져옵니다
        User member = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지않은 아이디입니다."));
        //토큰을 담을 Dto 객체를 만들고
        HeaderDto headerDto = new HeaderDto();
        //해당 dto안에있는 TOKEN값에 jwt를 생성하여 담습니다
        //현재 jwt에 저장되는 정보는 username과 id(pk)입니다
        headerDto.setTOKEN(jwtTokenProvider.createToken(member.getUsername(), member.getUserId()));
        //dto를 반환합니다. 후에 프론트에서 해당 dto에 담긴 token을 A-AUTH-TOKEN 헤더에 담아 전달해줄겁니다
        return headerDto;
    }


    @GetMapping("user/info")
    @ResponseBody
    //X-AUTH-TOKEN 헤더값을 확인합니다
    public Object getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        //헤더값이 비었다면 로그인되지 않았음을 알립니다
        if (userDetails == null) {
            return "로그인 상태가 아니거나 토큰이 만료되었습니다.";
        } else {
            //정상적인 토큰이라면 해당토큰에서 id와 username 정보를 읽어 보내줍니다.
            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setUser_id(userDetails.getUser().getUserId());
            userInfoDto.setUser_nickname(userDetails.getUser().getUsername());
            return userInfoDto;
        }
    }

    // 회원 가입 요청 처리
    @ApiOperation(value="회원 가입 요청", notes="회원 가입 요청합니다.")
    @PostMapping("/user/signup")
    public void registerUser(@Valid @RequestBody SignupRequestDto requestDto) {
        userService.registerUser(requestDto);
    }

    @ApiOperation(value="로그인 요청", notes="로그인 요청합니다.")
    @PostMapping("/user/login")
    public String login(@RequestBody SignupRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 유저입니다."));
        if (!encodePassword.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return jwtTokenProvider.createToken(user.getUsername(),user.getUserId());
    }
}
