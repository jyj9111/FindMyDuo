package com.idle.fmd.global.auth.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest
                .getClientRegistration()
                .getRegistrationId();

        // 전달받은 정보를 원하는데로 사용하기 위해 데이터 정리를 위한 attributes Map 생성
        Map<String, Object> attributes = new HashMap<>();
        String nameAttribute = "";

        // Naver 로직
        if (registrationId.equals("naver")) {
            attributes.put("provider", "naver");

            // 받은 사용자 데이터를 정리한다.
            Map<String, Object> responseMap = oAuth2User.getAttribute("response");
            attributes.put("id", responseMap.get("id"));
            attributes.put("name", responseMap.get("name"));
            attributes.put("email", responseMap.get("email"));
            attributes.put("phone", responseMap.get("mobile"));
            nameAttribute = "email";
        }

        // Kakao 로직
        if (registrationId.equals("kakao")) {
            attributes.put("provider", "kakao");

            // 받은 사용자 데이터를 정리한다.
            Map<String, Object> propMap = oAuth2User.getAttribute("properties");
            attributes.put("id", oAuth2User.getAttribute("id"));
            attributes.put("name", propMap.get("nickname"));
            Map<String, Object> accountMap
                    = oAuth2User.getAttribute("kakao_account");
            attributes.put("email", accountMap.get("email"));
            nameAttribute = "email";
        }

        // Google 로직
        if (registrationId.equals("google")) {
            attributes.put("provider", "google");

            // 받은 사용자 데이터를 정리한다.
            Map<String, Object> responseMap = oAuth2User.getAttributes();
            attributes.put("id", responseMap.get("sub"));
            attributes.put("name", responseMap.get("name"));
            attributes.put("email", responseMap.get("email"));
            nameAttribute = "email";
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                attributes,
                nameAttribute
        );
    }
}
