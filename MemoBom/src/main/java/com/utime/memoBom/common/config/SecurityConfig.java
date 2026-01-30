package com.utime.memoBom.common.config;

import static org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.withDefaults;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.utime.memoBom.common.security.JwtAuthenticationFilter;
import com.utime.memoBom.common.vo.EJwtRole;
import com.utime.memoBom.common.vo.WhiteAddressList;

import jakarta.annotation.Resource;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Resource(name="JwtAuthentication")
	private jakarta.servlet.Filter jwtAuthFilter;
	
	@Resource(name="jwtAuthenticationEntryPoint")
	private AuthenticationEntryPoint authenticationEntryPoint;
	
	@Resource(name="JwtAccessDenied")
	private AccessDeniedHandler accessDeniedHandler;
	
	@Autowired
	OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;
	
	@Autowired
	AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	
	@Autowired
	AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
	
	/**
     * 정적 자원에 대한 보안 필터 적용 제외 설정.<br/>
     * 필터 체인을 완전히 바이패스하므로 성능 최적화에 유리하며, 
     * 주로 public한 static resources(js, css, images 등)에 적용함.
     * * @return WebSecurityCustomizer
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
            .requestMatchers(
                "/manifest.webmanifest",
                "/sw.js",
                "/favicon.ico",
                "/Error/**"
            );
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    	PathPatternRequestMatcher.Builder matcher = withDefaults();

        // White list -> PathPatternRequestMatcher로 변환
        final List<PathPatternRequestMatcher> permitAllMatchers = Arrays.stream(WhiteAddressList.AddressList)
            .map(path -> {
                String pattern = path.endsWith("/") ? path + "**" : path;
                return matcher.matcher(pattern);
            })
            .toList();

        // 수동 추가
        final List<RequestMatcher> extended = new java.util.ArrayList<>(permitAllMatchers);
        extended.add(matcher.matcher("/Fragment/**"));
        extended.add(matcher.matcher("/Mosaic/**"));
        extended.add(matcher.matcher("/"));

        final RequestMatcher[] permitAllWhiteList = extended.toArray(RequestMatcher[]::new);
	
        http.authorizeHttpRequests(auth -> auth
        	    // 1) 로그인 사용자만(또는 ROLE_USER) 필요한 “구체 경로”를 먼저
        	    .requestMatchers(
        	    		  "/Fragment/Tessera.html"
        	    		, "/Fragment/Save.json"
        	    		, "/Mosaic/Ensemble.html"
        	    		, "/Mosaic/Save.json"
        	    		, "/Mosaic/Flow.json"
        	    		, "/Push/**"
        	    		, "/User/**"
        	    		, "/My/**"
        	    	).hasRole(EJwtRole.User.name())
//        	    hasAuthority   hasRole

        	    // 2) 그 다음에 공개 경로(광범위)를 permitAll
        	    .requestMatchers(permitAllWhiteList).permitAll()

        	    // 3) Admin
        	    .requestMatchers("/Lotus/**").hasRole(EJwtRole.Admin.name())

        	    // 4) 나머지 로그인(인증)된 사용자만 접근 가능
        	    .anyRequest().authenticated()
        	);

        
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
//                // 로그인 시도 URL
//                .authorizationEndpoint(a -> a.baseUri("/Auth/OAuth2"))
                // OAuth 서비스 업체 승인된 리디렉션 URI 주소. {도메인}/{contextPath}/Auth/OAuth2/callback/google
//                .redirectionEndpoint(r -> r.baseUri("/Auth/OAuth2/callback/*"))
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
            );
        
        http.formLogin(AbstractHttpConfigurer::disable);
        
        http.logout(AbstractHttpConfigurer::disable);
        
        http.csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
        	.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable)); // FrameOptions 비활성화

        http.sessionManagement(session -> session
        	    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        	);

        http.addFilterBefore(this.jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(conf -> conf
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .accessDeniedHandler(this.accessDeniedHandler)
            );
        
        return http.build();
    }
    
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthFilterRegistration(JwtAuthenticationFilter f) {
        FilterRegistrationBean<JwtAuthenticationFilter> reg = new FilterRegistrationBean<>(f);
        reg.setEnabled(false);
        return reg;
    }
}

