package com.utime.memoBom.common.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.utime.memoBom.common.vo.WhiteAddressList;
import com.utime.memoBom.user.vo.ResUserVo;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Order(1)
@Component("JwtAuthentication")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
    private JwtProvider jwtProvider;
	
	private final Set<String> whiteListPaths = Arrays.stream(WhiteAddressList.AddressList).collect(Collectors.toSet());
    
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
    	 
    	final String path = request.getRequestURI().substring( request.getContextPath().length() );
    	if( path.length() <= 1) {
    		return true;
    	}
    	
    	final boolean result = whiteListPaths.stream().anyMatch(path::startsWith);
    	log.info("{} -> {}", path, result);
    	return result;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
    	
    	
    	final ResUserVo tokenRes = jwtProvider.reissueAccessToken(request, response);
    	if (tokenRes == null || tokenRes.getUser() == null) {

            // ✅ 여기서 강제 로그아웃 처리(쿠키 삭제 + SecurityContext 정리 + (선택)세션 무효화)
            forceLogout(request, response);

            // 보통은 401로 끝내는 게 UX/보안상 깔끔합니다.
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        	filterChain.doFilter(request, response);
            return;
        }
    	
		log.info(request.getRequestURI());
	    this.authenticateUser( tokenRes.getUser() );
    	
    	filterChain.doFilter(request, response);
    }
    
    /**
     * SecurityContext에 사용자 정보 저장
     * @param token
     */
    private void authenticateUser(UserVo user) {

        if (user == null) return;

        // 이미 인증 정보가 있으면 덮어쓰지 않음
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ( auth != null && auth instanceof CustomUserDetails ) {
            return;
        }

        final CustomUserDetails principal = CustomUserDetails.from(user);
        if (principal == null) return;

        log.info("Setting Authentication for userNo: {}", principal.getUserNo());

        final Authentication authToken = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
    
    private void forceLogout(HttpServletRequest request, HttpServletResponse response) {
    	
    	log.warn("✅ 여기서 강제 로그아웃 처리");
        
    	// 1) 토큰 쿠키 제거 (중요)
        jwtProvider.procLogout(request, response);

        // 2) SecurityContext 정리 + (세션 쓰면) 세션 무효화까지
        new SecurityContextLogoutHandler().logout(
                request,
                response,
                SecurityContextHolder.getContext().getAuthentication()
        );

        // 3) (중복이지만 안전) ThreadLocal 정리
        SecurityContextHolder.clearContext();
    }
}
