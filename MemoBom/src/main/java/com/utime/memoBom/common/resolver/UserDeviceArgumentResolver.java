package com.utime.memoBom.common.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.UserDevice;


@Component("UserDeviceArgumentResolver")
class UserDeviceArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest request,
			WebDataBinderFactory factory) throws Exception {
		
		return AppUtils.getDeviceInfoFromUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(UserDevice.class);
	}
	
}
