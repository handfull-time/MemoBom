package com.utime.memoBom.common.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 공통 처리 작업
 */
@ControllerAdvice
public class GlobalModelAttributes {

	@ModelAttribute("currentPath")
	public String currentPath(HttpServletRequest request) {
	    String uri = request.getRequestURI();      // /MemoBom/My/index.html
	    String ctx = request.getContextPath();     // /MemoBom
	    return (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) ? uri.substring(ctx.length()) : uri;
	}

}
