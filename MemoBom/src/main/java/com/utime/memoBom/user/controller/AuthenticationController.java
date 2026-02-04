package com.utime.memoBom.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.AppDefine;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("Auth")
@RequiredArgsConstructor
public class AuthenticationController {
	
//	/**
//	 * 메타 처리
//	 * @param model
//	 * @param redirectUrl 이전 호출 됐던 URL
//	 * @return
//	 */
//	@GetMapping("NoneAuthMeta.html")
//    public String noneAuthMetaPage(ModelMap model, 
//    		@RequestParam(required = false) String redirectUrl) {
//		
//		model.addAttribute("redirectUrl", redirectUrl);
//		model.addAttribute("title", title);
//		model.addAttribute("description", title);
//		model.addAttribute("url", title);
//		
//		return "Auth/NoneAuthMeta";
//    }
	
	
	/**
	 * 로그인 화면
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("Login.html")
    public String loginPage( HttpServletRequest request, ModelMap model, LoginUser user,
    		@RequestParam(required = false) String error,
    		@RequestParam(required = false) String message,
    		@RequestParam(required = false) String returnUrl
    		) {
		
		if( user != null ) {
			log.info( "이미 로그인 돼 있는 회원 {}-{}", user.userNo(), user.uid() );
			return "redirect:/";
		}
		
		model.addAttribute("error", error );
		model.addAttribute("message", message );
		model.addAttribute(AppDefine.KeyShowHeader, false);
	    model.addAttribute(AppDefine.KeyShowFooter, false);
	    model.addAttribute(AppDefine.KeyLoadScript, false );
	    
	    if( AppUtils.isNotEmpty(returnUrl)) {
	    	// 로그인 후 이전 페이지로 이동
	    	request.getSession().setAttribute(AppDefine.KeyBeforeUri, returnUrl);
	    }
	    
        return "Auth/Login";
    }
	
}

