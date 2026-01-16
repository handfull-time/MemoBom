package com.utime.memoBom.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.utime.memoBom.common.vo.EJwtRole;

import java.util.Collections;
import java.util.Map;

/**
 * 구글 인증 토큰을 받은 직후 (인증 프로세스 내부)
 * userRequest를 통해 구글 API 호출 → OAuth2User 객체 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    	// 1. 구글 서버에서 사용자 정보 가져오기
        final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        final OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 2. 서비스 제공자 구분 (google, naver 등)
        final String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("Provider {}", registrationId);
        
        // 3. OAuth2 로그인 진행 시 키가 되는 필드값 (PK 역할)
        // 구글은 "sub"가 기본 PK입니다.
        final String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        
        log.info("userNameAttributeName {}", userNameAttributeName);

        // 4. 사용자 정보 속성 맵
        final Map<String, Object> attributes = oAuth2User.getAttributes();

        log.info("Google Login Success: {}", attributes.get("email"));

        // 6. SecurityContext에 저장할 User 객체 반환
        // JWT 생성을 위해 attributes를 그대로 리턴하거나, DTO로 변환하여 리턴합니다.
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority( EJwtRole.User.name() )), // 기본 권한 설정  "ROLE_USER"
                attributes,
                userNameAttributeName
        );
    }
}
