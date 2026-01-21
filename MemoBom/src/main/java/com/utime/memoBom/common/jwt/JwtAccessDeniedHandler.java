package com.utime.memoBom.common.jwt;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.utime.memoBom.common.util.AppUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("JwtAccessDenied")
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
	
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
    	log.warn("접근 권한이 없는 사용자입니다( AccessDeniedHandler )");
    	log.warn("요청 URL : " + request.getRequestURI() );
    	log.warn("Auth = {}", SecurityContextHolder.getContext().getAuthentication());

    	response.setStatus( HttpServletResponse.SC_FORBIDDEN );
    	
    	if (AppUtils.isAjaxRequest(request)) {
    		response.setContentType("application/json;charset=UTF-8");
    	    response.getWriter().write("{\"message\":\"Status code (403) indicating the server understood the request but refused to fulfill it.\"}");
    	} else {
    		response.sendRedirect( request.getContextPath() + "/Error/AccessDenied.html?url=" + URLEncoder.encode(request.getRequestURI(), StandardCharsets.UTF_8));
    	}
    }
}
