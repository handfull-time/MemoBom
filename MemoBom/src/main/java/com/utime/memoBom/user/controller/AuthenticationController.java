package com.utime.memoBom.user.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.service.AuthService;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("Auth")
@RequiredArgsConstructor
public class AuthenticationController {
	
	private final AuthService authService;
//	
	/**
	 * 메타 처리
	 * @param model
	 * @param redirectUrl 이전 호출 됐던 URL
	 * @return
	 */
	@GetMapping("NoneAuthMeta.html")
    public String noneAuthMetaPage(ModelMap model, @RequestParam(required = false) String redirectUrl) {
		
		model.addAttribute("redirectUrl", redirectUrl);
        
		return "Auth/NoneAuthMeta";
    }
	
	
	/**
	 * 로그인 화면
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("Login.html")
    public String loginPage( HttpServletRequest request, ModelMap model, UserVo user,
    		@RequestParam(required = false) String error,
    		@RequestParam(required = false) String message) {
		
		if( user != null ) {
			log.info( "이미 로그인 돼 있는 회원 {}", user.getId() );
			return "redirect:/Board/index.html";
		}
		
		model.addAttribute("error", error );
		model.addAttribute("message", message );
		model.addAttribute(AppDefine.KeyShowHeader, false);
	    model.addAttribute(AppDefine.KeyShowFooter, false);
		
        return "Auth/Login";
    }
	
	@PostMapping("Logout.json")
    public ReturnBasic logout( HttpServletRequest request, HttpServletResponse response, UserVo user) {
		
        return authService.logout(request, response, user);
    }
	
	@GetMapping("Withdraw/Google")
	public void startWithdrawGoogle(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
	    final Cookie cookie = new Cookie(AppDefine.WithdrawMode, "true");
	    cookie.setPath("/");
	    cookie.setMaxAge(300); // 300초
	    response.addCookie(cookie);

	    // 2. 구글 로그인 페이지로 강제 이동 (재로그인 유도)
	    response.sendRedirect(request.getContextPath() + "/oauth2/authorization/google");
	}
}

