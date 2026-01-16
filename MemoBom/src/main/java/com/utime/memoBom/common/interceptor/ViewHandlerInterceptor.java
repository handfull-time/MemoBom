package com.utime.memoBom.common.interceptor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("ViewInterceptor")
class ViewHandlerInterceptor implements AsyncHandlerInterceptor {

	@Override
	public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView view)
			throws Exception {
		
		if( ! (handler instanceof HandlerMethod) )
			return;
		
		if( view == null )
			return;

		final ModelMap model = view.getModelMap();
		
		final String uri = req.getRequestURI();
		final String contextPath = req.getContextPath();
		model.addAttribute("currentURI", uri.substring(contextPath.length()) );
		
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {
			final Object obj = authentication.getPrincipal();
			if( obj != null && obj instanceof UserVo ) {
				model.addAttribute(AppDefine.KeyParamUser, obj );
			}
        }
	}
}
