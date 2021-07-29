package com.mealchak.mealchakserverapplication.oauth2;

import com.mealchak.mealchakserverapplication.oauth2.provider.KakaoUserInfo;
import org.json.JSONObject;
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
        KakaoUserInfo userInfo = getUserInfoByToken(accessToken);
        return userInfo;
    }


    public String getAccessToken(String authorizedCode) {
        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "7bdd85c1e8d3b04bfc556d4b741605ec");
        //테스트를 위한 url설정
//        params.add("redirect_uri", "http://localhost:8080/user/kakao/callback");
        params.add("redirect_uri", "http://localhost:3000/user/kakao/callback");
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
        String accessToken = rjson.getString("access_token");

        return accessToken;
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
        String thumbnailImg = "https://s3.us-west-2.amazonaws.com/secure.notion-static.com/f6d160ed-a475-4b84-b8a9-2d47685e12ac/profileDefaultImg.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20210729%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20210729T013750Z&X-Amz-Expires=86400&X-Amz-Signature=ce9e2e1ef306a4e1223e05e5482854465231472911851c226bc4e93fed2b47c2&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22profileDefaultImg.jpg%22";
        String profileImg = "https://s3.us-west-2.amazonaws.com/secure.notion-static.com/f6d160ed-a475-4b84-b8a9-2d47685e12ac/profileDefaultImg.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20210729%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20210729T013750Z&X-Amz-Expires=86400&X-Amz-Signature=ce9e2e1ef306a4e1223e05e5482854465231472911851c226bc4e93fed2b47c2&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22profileDefaultImg.jpg%22";
        try {
            thumbnailImg = body.getJSONObject("properties").getString("thumbnail_image");
            profileImg = body.getJSONObject("properties").getString("profile_image");
            Exception e = new Exception("프로필 없음");
            throw e;
        } catch (Exception e) {
        }
        return new KakaoUserInfo(id, email, nickname, thumbnailImg, profileImg);
    }
}