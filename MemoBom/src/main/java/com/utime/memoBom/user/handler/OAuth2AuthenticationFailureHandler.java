package com.utime.memoBom.user.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.utime.memoBom.common.jwt.JwtProvider;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final JwtProvider jwtProvider;
	
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
        // 로그아웃 처리 (브라우저에 남은 JWT 쿠키 삭제)
        jwtProvider.procLogout(request, response);

        // 1. 에러 로그 남기기 (서버 관리용)
        // 구체적인 에러 메시지를 남겨야 나중에 디버깅이 편합니다.
        log.error("OAuth2 Login Failed: {}", exception.getMessage());
        
        // 2. 리다이렉트 URL 생성
        // 로그인 페이지(/Auth/Login)로 돌려보내되, URL 파라미터로 error 메시지를 붙입니다.
        // 한글이나 특수문자가 있을 수 있으므로 인코딩 처리를 권장합니다.
        String targetUrl = UriComponentsBuilder.fromUriString("/Auth/Login.html") // 포파님이 만드신 로그인 페이지 경로
                .queryParam("error", "social_login_fail")
                .queryParam("message", exception.getLocalizedMessage()) // 보안상 구체적인 메시지는 숨기고 "Login Failed" 등으로 고정해도 됩니다.
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();

        // 3. 리다이렉트 수행
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}