package com.utime.memoBom.common.interceptor;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("LogInterceptor")
class LogHandlerInterceptor implements AsyncHandlerInterceptor {

//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//	    String sessionId = request.getSession().getId();
//	    MDC.put("sessionId", sessionId); // 로그에 세션 ID 주입
//	    return true;
//	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
	    MDC.put("sessionId", request.getSession().getId());
		return AsyncHandlerInterceptor.super.preHandle(request, response, handler);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
		MDC.clear();
		AsyncHandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}

}
