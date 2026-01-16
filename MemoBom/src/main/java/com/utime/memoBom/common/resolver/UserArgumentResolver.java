package com.utime.memoBom.common.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.utime.memoBom.common.jwt.JwtProvider;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;

@Component("UserArgument")
public class UserArgumentResolver implements HandlerMethodArgumentResolver{

	@Autowired
    private JwtProvider jwtProvider;
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(UserVo.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Object result = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserVo) {
        	result = authentication.getPrincipal();
        }
        
        if( result == null ) {
        	
            final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

            if (request != null ) {
            	result = jwtProvider.getUserVoAccessToken(request);
            }
        }
        
        return result;
	}

}
