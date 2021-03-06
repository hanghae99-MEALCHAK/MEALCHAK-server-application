package com.mealchak.mealchakserverapplication.oauth2;

import com.mealchak.mealchakserverapplication.oauth2.provider.KakaoUserInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoOAuth2 {
    public KakaoUserInfo getUserInfo(String authorizedCode) {
        // 1. 인가코드 -> 액세스 토큰
        String accessToken = getAccessToken(authorizedCode);
        // 2. 액세스 토큰 -> 카카오 사용자 정보
        return getUserInfoByToken(accessToken);
    }

    @Value("${spring.datasource.client_id}")
    private String client_id;

    @Value("${spring.datasource.redirect_uri}")
    private String redirect_uri;

    public String getAccessToken(String authorizedCode) {
        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client_id);
        // redirect url 설정
        params.add("redirect_uri", redirect_uri);
        params.add("code", authorizedCode);

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        // Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON -> 액세스 토큰 파싱
        String tokenJson = response.getBody();
        JSONObject rjson = new JSONObject(tokenJson);

        return rjson.getString("access_token");
    }

    private KakaoUserInfo getUserInfoByToken(String accessToken) {
        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        // Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        JSONObject body = new JSONObject(response.getBody());
        Long id = body.getLong("id");
        String email = body.getJSONObject("kakao_account").getString("email");
        String nickname = body.getJSONObject("properties").getString("nickname");
        String age = null;
        String gender = null;
        String profileImg = "https://gorokke.shop/image/profileDefaultImg.jpg"; // AWS EC2
//        String profileImg = "http://115.85.182.57/image/profileDefaultImg.jpg";  // NAVER EC2

        // 넘어온 유저정보에서 프로필사진 / 연령 / 성별 정보를 추가로 처리함
        try {
            profileImg = body.getJSONObject("properties").getString("profile_image");
            throw new Exception("프로필 사진 없음 기본 이미지로 대체");
        } catch (Exception ignored) {}
        try {
            age = body.getJSONObject("kakao_account").getString("age_range");
            throw new Exception("프로필 나이대 제공 동의 없음");
        } catch (Exception ignored) {}
        try {
            gender = body.getJSONObject("kakao_account").getString("gender");
            throw new Exception("프로필 성별 제공 동의 없음");
        } catch (Exception ignored) {}

        return new KakaoUserInfo(id, email, nickname, profileImg, age, gender);
    }
}