package com.utime.memoBom.common.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.AppDefine;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("jwtAuthenticationEntryPoint")
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
    	
    	final String requestUri = request.getRequestURI();
    	
    	if( AppUtils.isBot(request.getHeader(HttpHeaders.USER_AGENT))) {
    		log.info("Bot 진입 : " + request.getHeader(HttpHeaders.USER_AGENT));
    		
    		String originalUrl = requestUri;
    		final String query = request.getQueryString();
            if (query != null) {
                originalUrl += "?" + query;
            }

            // URL 인코딩
            final String redirectUrl = URLEncoder.encode(originalUrl, StandardCharsets.UTF_8.name());

//            response.sendRedirect(request.getContextPath() + "/Auth/NoneAuthMeta.html?redirectUrl=" + redirectUrl);
            response.sendRedirect(request.getContextPath() + "/Error/AccessDenied.html?url=" + redirectUrl);
            
    		return;
    	}

    	final int status = response.getStatus();
        
    	log.warn( "Url:{}\tStatus:{}\tMessage:{}", requestUri, status, authException.getMessage() );
    	
    	if (AppUtils.isAjaxRequest(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        } else {
        	final String contextPath = request.getContextPath();
        	final String beforUri = requestUri.substring( contextPath.length() );
        	request.getSession().setAttribute(AppDefine.KeyBeforeUri, beforUri);
            response.sendRedirect(contextPath + "/Auth/Login.html");
        }
    }
    

}