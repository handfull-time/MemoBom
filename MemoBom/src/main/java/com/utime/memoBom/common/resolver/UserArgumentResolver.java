package com.utime.memoBom.common.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.utime.memoBom.common.security.CustomUserDetails;
import com.utime.memoBom.common.security.LoginUser;

@Component("UserArgumentResolver")
public class UserArgumentResolver implements HandlerMethodArgumentResolver{

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return LoginUser.class.equals(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;

        final Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails detail)) return null;

        return new LoginUser(detail.getUserNo(), detail.getUid(), detail.getRole());
	}

}
