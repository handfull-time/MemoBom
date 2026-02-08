package com.utime.memoBom.admin.controller;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.utime.memoBom.admin.service.AdminService;
import com.utime.memoBom.common.security.LoginUser;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("Lotus")
@RequiredArgsConstructor
public class AdminController {
	
	final AdminService adminService;
	
	@Value("${env.admin.userNo}")
	private String adminUserNo;
	
	private Set<Long> adminUserNoSet;
	
	@PostConstruct
	private void init() {

		this.adminUserNoSet = Arrays.stream(adminUserNo.split(","))
		        .map(String::trim)
		        .filter(s -> s.matches("\\d+"))
		        .map(Long::valueOf)
		        .collect(Collectors.toSet());
	}
	
	@GetMapping(path = {"Login.html" })
    public String adminLogin( ModelMap model, LoginUser user ) {
		if( this.adminUserNoSet.contains(user.userNo()) ) {
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

