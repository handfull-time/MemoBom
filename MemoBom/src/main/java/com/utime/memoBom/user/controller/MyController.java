package com.utime.memoBom.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.service.UserService;
import com.utime.memoBom.user.vo.UserVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("My")
@RequiredArgsConstructor
public class MyController {

	final UserService userService;
	/**
	 * MyPage 화면
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(path = {"", "/", "index.html" })
    public String myMain( UserVo user ) {
		
		if( user == null ) {
			return "redirect:/Auth/Login.html";
		}else { 			
			return "My/MyPage";
		}
	}
	
	@GetMapping(path = "Fragments.html")
    public String myFragments(Model model) {
		return "My/MyFragments";
    }
	
	@GetMapping(path = "Mosaic.html")
    public String myMosaic(Model model) {
		return "My/MyMosaic";
    }
	
	@GetMapping(path = "Calendar.html")
    public String myCalendar( Model model) {
		return "My/MyCalendar";
    }
	
	@ResponseBody
	@GetMapping(path = "MyCalendar.json")
    public ReturnBasic myCalendar( UserVo user, @RequestParam String date ) {
		
		return userService.getMyWriteDataList( user, date );
    }
	
	
}
