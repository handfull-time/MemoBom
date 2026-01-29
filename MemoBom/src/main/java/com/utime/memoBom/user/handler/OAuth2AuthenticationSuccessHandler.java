package com.utime.memoBom.user.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.utime.memoBom.common.security.JwtProvider;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.EJwtRole;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    private final UserDao userDao;
    
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // 로그아웃 처리 (브라우저에 남은 JWT 쿠키 삭제)
        jwtProvider.procLogout(request, response);

        // 1. Authentication을 OAuth2AuthenticationToken으로 캐스팅
        final OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;

        // 2. Registration ID 추출 (google, naver, kakao 등 application.yml에 적은 이름)
        final String provider = authToken.getAuthorizedClientRegistrationId();

        log.info("로그인한 플랫폼: {}", provider); // 결과 예: "google"
    	
    	// 1. 구글 인증 정보 가져오기
        final OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        
        final Map<String, Object> attributes = oAuth2User.getAttributes();
        {
        	// for log
        	final StringBuffer sb = new StringBuffer("\n");
            attributes.forEach((key, value) -> {
                sb.append( "Key: ["+key+"] / Value: ["+value+"]\n");
            });
            log.info( sb.toString() );
        }
        
        final OAuth2AuthorizedClient client = authorizedClientRepository.loadAuthorizedClient(
        		provider, 
                authentication, 
                request
        );

        final String googleAccessToken = client.getAccessToken().getTokenValue();
        
        // "탈퇴 모드" 쿠키가 있는지 확인
        boolean isWithdraw = false;
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (AppDefine.WithdrawMode.equals(c.getName())) {
                    isWithdraw = true;
                    // 확인했으니 쿠키 삭제 (MaxAge 0)
                    c.setMaxAge(0);
                    c.setPath("/");
                    response.addCookie(c);
                    break;
                }
            }
        }
        
        // 2. DB 처리 (기존 회원 확인 및 자동 가입)
        UserVo userVo;
		try {
			userVo = this.processUserLogin(request, provider, attributes);
		} catch (Exception e) {
			log.error("processUserLogin 작업 오류", e);
			throw new IOException(e);
		}

        
        if (isWithdraw && userVo != null ) {
        	log.info("회원 탈퇴 정보다.");
        	
        	// 구글 API 호출해서 끊기
        	this.unlinkGoogle(googleAccessToken);

            // DB에서 회원 삭제
            try {
				userDao.deleteUser(userVo);
			} catch (Exception e) {
				log.error("", e);
			} 

            // 다시 로그인 페이지로 이동
            getRedirectStrategy().sendRedirect(request, response, "/Auth/Login.html");
            return;
        }
       
        if (userVo == null) {
            // 치명적 에러: DB 처리 실패 시 로그인 페이지로 리다이렉트
        	final String targetUrl = UriComponentsBuilder.fromUriString("/Auth/Login.html")
                    .queryParam("error", "true")
                    .queryParam("message", "User Data Processing Failed")
                    .encode(StandardCharsets.UTF_8)
                    .build().toUriString();
            this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
            return;
        }

        // 3. JwtProvider를 이용해 토큰 발급 및 쿠키 설정 (핵심 수정 부분)
        // 기존의 수동 쿠키 생성 코드를 모두 제거하고 이 한 줄로 처리합니다.
        ReturnBasic result;
		try {
			result = jwtProvider.procLogin(request, response, userVo);
		} catch (Exception e) {
			log.error("", e);
			result = new ReturnBasic("E", e.getLocalizedMessage());
		}

        if (result.isError()) {
             log.error("JWT Issue Failed: {}", result.getMessage());
             
             final String targetUrl = UriComponentsBuilder.fromUriString("/Auth/Login.html")
                     .queryParam("error", "true")
                     .queryParam("message", result.getMessage())
                     .encode(StandardCharsets.UTF_8)
                     .build().toUriString();
             this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
             return;
        }

        // 4. 로그인 성공 후 메인 페이지로 이동
        log.info("Google Login Success: {}", userVo.getId());
        
    	String url;
    	final Object obj = request.getSession().getAttribute(AppDefine.KeyBeforeUri);
    	if( obj != null ) {
    		request.getSession().removeAttribute(AppDefine.KeyBeforeUri);
    		url = (String)obj;
    	}else {
    		url = "/Fragment/index.html";
    	}
    	
        getRedirectStrategy().sendRedirect(request, response, url);
    }

    private final String KeyProviderGoogle = "google";
    
    /**
     * DB에서 회원을 조회하거나, 없으면 생성하는 로직
     * @throws Exception 
     */
    private UserVo processUserLogin(HttpServletRequest request, String provider, Map<String, Object> attributes) throws Exception {
    	
    	UserVo result = null;
    	
    	if( KeyProviderGoogle.equals(provider) ) {
    		final String providerId = (String) attributes.get("sub");
    		result = userDao.findById(provider, providerId);
    		
    		if( result != null ) {
    			return result;
    		}
    		
    		result = new UserVo();
    		result.setId(providerId);
    		result.setProvider(provider);
    		result.setEnabled(true);
    		result.setEmail((String) attributes.get("email") );
    		result.setRole(EJwtRole.User);
    		result.setNickname( (String) attributes.get("name") );
    		result.setProfileUrl( (String) attributes.get("picture") );
    		
    		log.info("사용자 추가 정보 : {}", result);
    		
    		userDao.addUser(result);
    	}
		return result;
    }
    
    /**
     * 구글 연동 해제 요청 메서드
     * @param accessToken
     */
    private void unlinkGoogle(String accessToken) {
    	
    	log.info("GOOGLE 해제 요청 ");
    	
        final String url = "https://oauth2.googleapis.com/revoke?token=" + accessToken;
        
        try {
        	final RestTemplate restTemplate = new RestTemplate();
        	final String res = restTemplate.postForObject(url, null, String.class);
        	
            log.info("GOOGLE REVOKE SUCCESS.\nResponse:{}", res);
        } catch (Exception e) {
            log.error("GOOGLE REVOKE FAILED: {}", e.getMessage());
        }
    }
}