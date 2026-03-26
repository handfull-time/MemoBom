package com.utime.memoBom.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
			return "redirect:/Lotus/index.html";
		}else {
			return "redirect:/";
		}
    }
	
	@GetMapping(path = {"Logout.html" })
    public String adminLogout( HttpServletRequest request, HttpServletResponse response, ModelMap model, LoginUser user ) throws Exception {

		this.adminService.adminLogout(request, response);
		
		return "redirect:/";
    }

	/**
	 * 관리자 메인 -대쉬 보드
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(path = {"", "/", "index.html" })
    public String adminMain( ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("item", adminService.getDashboard(date) );
		model.addAttribute("date", date );
		
		return "Admin/AdminDashBoard";
    }

	@GetMapping(path = {"User.html" })
    public String adminUsers(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		return "Admin/AdminUser";
    }
	
	@GetMapping(path = {"Topic.html" })
    public String adminTopics(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		return "Admin/AdminTopic";
    }
	
	@GetMapping(path = {"Fragment.html" })
    public String adminFragments(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		return "Admin/AdminFragment";
    }
	
	@GetMapping(path = {"Comment.html" })
    public String adminComments(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		return "Admin/AdminComment";
    }
	
	@GetMapping(path = {"Emotion.html" })
    public String adminEmotions(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		return "Admin/AdminEmotion";
    }
	
	@GetMapping(path = {"Scrap.html" })
    public String adminScrap(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		return "Admin/AdminScrap";
    }
	
	@GetMapping(path = {"Share.html" })
    public String adminShares(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		return "Admin/AdminShare";
    }
	
	@GetMapping(path = {"Push.html" })
    public String adminPush(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		return "Admin/AdminPush";
    }
	
	@GetMapping(path = {"Setting.html" })
    public String adminSettings(ModelMap model, LoginUser user ) {
		
		return "Admin/AdminSetting";
    }
	
}

