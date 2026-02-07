package com.utime.memoBom.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.utime.memoBom.admin.service.AdminService;
import com.utime.memoBom.user.vo.UserVo;

import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("Lotus")
@RequiredArgsConstructor
public class AdminController {
	
	final AdminService adminService;
	
	/**
	 * 관리자 화면
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(path = {"", "/", "index.html" })
    public String adminMain( ModelMap model, UserVo user ) {
		
		return "Admin/AdminMain";
    }

}

