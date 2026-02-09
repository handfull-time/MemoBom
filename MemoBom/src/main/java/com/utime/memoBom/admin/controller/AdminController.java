package com.utime.memoBom.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.utime.memoBom.admin.service.AdminService;
import com.utime.memoBom.common.security.LoginUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("Lotus")
@RequiredArgsConstructor
public class AdminController {
	
	final AdminService adminService;
	
	@GetMapping(path = {"Login.html" })
    public String adminLogin( HttpServletRequest request, HttpServletResponse response, ModelMap model, LoginUser user ) throws Exception {

		if( ! this.adminService.adminLogin(request, response, user).isError() ) {
			return "Admin/AdminMain";
		}else {
			return "redirect:/";
		}
    }

	/**
	 * 관리자 화면
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(path = {"", "/", "index.html" })
    public String adminMain( ModelMap model, LoginUser user ) {
		
		return "Admin/AdminMain";
    }

}

