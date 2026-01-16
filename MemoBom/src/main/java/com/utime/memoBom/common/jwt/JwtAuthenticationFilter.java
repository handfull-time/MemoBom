package com.utime.memoBom.common.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    	return whiteListPaths.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
    	
    	final ResUserVo tokenRes = jwtProvider.reissueAccessToken(request, response);
    	if( tokenRes.isError() ) {
    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    		return;
    	}
    	
		log.info(request.getRequestURI());
	    this.authenticateUser( tokenRes.getUser() );
    	
    	filterChain.doFilter(request, response);
    }
    
    private String rolePrefix = "ROLE_";
    
    /**
     * SecurityContext에 사용자 정보 저장
     * @param token
     */
    private void authenticateUser(UserVo user) {
    	
        if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Setting Authentication for user: {}", user.getId());
            
            final Authentication authToken = new UsernamePasswordAuthenticationToken(user, null,
                    Collections.singleton( new SimpleGrantedAuthority(rolePrefix + user.getRole().name()) ));
            
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
}
