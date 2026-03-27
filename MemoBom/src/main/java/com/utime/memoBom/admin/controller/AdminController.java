package com.utime.memoBom.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.admin.service.AdminService;
import com.utime.memoBom.admin.vo.AdminSearchVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.ReturnBasic;

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

		model.addAttribute("date", date );
		return "Admin/AdminUser";
    }
	
	@ResponseBody
	@GetMapping(path = {"UserList.json" })
	public ReturnBasic getUserList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getUserList(user, searchVo );
	}
	
	@GetMapping(path = {"Topic.html" })
    public String adminTopics(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("date", date );
		return "Admin/AdminTopic";
    }
	
	@ResponseBody
	@GetMapping(path = {"TopicList.json" })
	public ReturnBasic getTopicList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getTopicList(user, searchVo );
	}
	
	@GetMapping(path = {"Fragment.html" })
    public String adminFragments(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("date", date );
		return "Admin/AdminFragment";
    }
	
	@ResponseBody
	@GetMapping(path = {"FragmentList.json" })
	public ReturnBasic getFragmentList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getFragmentList(user, searchVo );
	}
	
	@GetMapping(path = {"Comment.html" })
    public String adminComments(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("date", date );
		return "Admin/AdminComment";
    }
	
	@ResponseBody
	@GetMapping(path = {"CommentList.json" })
	public ReturnBasic getCommentList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getCommentList(user, searchVo );
	}
	
	@GetMapping(path = {"Emotion.html" })
    public String adminEmotions(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("date", date );
		return "Admin/AdminEmotion";
    }
	
	@ResponseBody
	@GetMapping(path = {"EmotionList.json" })
	public ReturnBasic getEmotionList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getEmotionList(user, searchVo );
	}
	
	@GetMapping(path = {"Scrap.html" })
    public String adminScrap(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("date", date );
		return "Admin/AdminScrap";
    }
	
	@ResponseBody
	@GetMapping(path = {"ScrapList.json" })
	public ReturnBasic getScrapList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getScrapList(user, searchVo );
	}
	
	@GetMapping(path = {"Share.html" })
    public String adminShares(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("date", date );
		return "Admin/AdminShare";
    }
	
	@ResponseBody
	@GetMapping(path = {"ShareList.json" })
	public ReturnBasic getShareList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getShareList(user, searchVo );
	}
	
	@GetMapping(path = {"Push.html" })
    public String adminPush(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("date", date );
		return "Admin/AdminPush";
    }
	
	@ResponseBody
	@GetMapping(path = {"PushList.json" })
	public ReturnBasic getPushList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getPushList(user, searchVo );
	}
	
	@GetMapping(path = {"Setting.html" })
    public String adminSettings(ModelMap model, LoginUser user ) {
		
		return "Admin/AdminSetting";
    }
	
}

