package com.utime.memoBom.common.config;

import static org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.withDefaults;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

import com.utime.memoBom.common.vo.EJwtRole;
import com.utime.memoBom.common.vo.WhiteAddressList;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;


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
        // (주의) "/File/Stream"만 허용이면 그대로, 하위까지면 "/File/Stream/**"
        final List<RequestMatcher> extended = new java.util.ArrayList<>(permitAllMatchers);
        extended.add(matcher.matcher("/Fragment/**"));
        extended.add(matcher.matcher("/Mosaic/**"));

        final RequestMatcher[] permitAllWhiteList = extended.toArray(RequestMatcher[]::new);
	
//        http.authorizeHttpRequests(auth -> auth
//                .requestMatchers( permitAllWhiteList ).permitAll() // 누구나 접근 가능.
//        	    .requestMatchers("/Admin/**").hasRole(EJwtRole.Admin.name()) // 어드민이 갈 곳. 
//        	    .requestMatchers("/User/**", "/Fragment/**", "/Topic/**", "/Push/**").hasRole(EJwtRole.User.name()) // 일반 유저가 갈 곳.
//                .anyRequest().authenticated()
//            );
        
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
        	    	).hasRole(EJwtRole.User.name())

        	    // 2) 그 다음에 공개 경로(광범위)를 permitAll
        	    .requestMatchers(permitAllWhiteList).permitAll()

        	    // 3) Admin
        	    .requestMatchers("/Spring/**").hasRole(EJwtRole.Admin.name())

        	    // 4) 나머지 로그인(인증)된 사용자만 접근 가능
        	    .anyRequest().authenticated()
        	);

        
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
        );
        
        http.formLogin(AbstractHttpConfigurer::disable);
        
        http.logout(AbstractHttpConfigurer::disable);
        
        http
        .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
        .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable)); // FrameOptions 비활성화

        http.sessionManagement(session -> session
        		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        	);

        http.addFilterBefore(this.jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(conf -> conf
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .accessDeniedHandler(this.accessDeniedHandler)
            );
        
        return http.build();
    }
    
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .authorizeHttpRequests(auth -> auth
//                .anyRequest().permitAll()
//            )
//            .csrf(AbstractHttpConfigurer::disable)
//            .formLogin(AbstractHttpConfigurer::disable)
//            .httpBasic(AbstractHttpConfigurer::disable);
//        
//        return http.build();
//    }

}
