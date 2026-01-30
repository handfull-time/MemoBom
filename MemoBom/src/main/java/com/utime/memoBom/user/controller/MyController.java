package com.utime.memoBom.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.dto.MySearchDto;
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
    public String myMain( LoginUser user ) {
		
		if( user == null ) {
			return "redirect:/Auth/Login.html";
		}else { 			
			return "My/MyPage";
		}
	}
	
	@GetMapping(path = "Alarm.html")
    public String myAlarm(Model model) {
		return "My/MyAlarm";
    }
	
	@ResponseBody
	@GetMapping(path = "Alarm.json")
    public ReturnBasic myAlarm( LoginUser user, MySearchDto searchVo ) {
		
		return userService.getMyAlarmDataList( user, searchVo );
    }

	@GetMapping(path = "Fragments.html")
    public String myFragments(Model model ) {
		return "My/MyFragments";
    }
	
	@ResponseBody
	@GetMapping(path = "Fragments.json")
    public ReturnBasic myFragments( LoginUser user, MySearchDto searchVo ) {
		
		return userService.getMyFragmentsDataList( user, searchVo );
    }

	@GetMapping(path = "Mosaic.html")
    public String myMosaic(Model model) {
		return "My/MyMosaic";
    }
	
	@ResponseBody
	@GetMapping(path = "MyMosaic.json")
    public ReturnBasic myMosaic( LoginUser user, MySearchDto searchVo ) {
		
		return userService.getMyMosaicDataList( user, searchVo );
    }

	@GetMapping(path = "Comments.html")
    public String myComments(Model model) {
		return "My/MyComments";
    }
	
	@ResponseBody
	@GetMapping(path = "MyComments.json")
    public ReturnBasic myComments( LoginUser user, MySearchDto searchVo ) {
		
		return userService.getMyCommentsDataList( user, searchVo );
    }

	@GetMapping(path = "Calendar.html")
    public String myCalendar( Model model) {
		return "My/MyCalendar";
    }
	
	@ResponseBody
	@GetMapping(path = "MyCalendar.json")
    public ReturnBasic myCalendar( LoginUser user, @RequestParam String date ) {
		
		return userService.getMyCalendarDataList( user, date );
    }
	
	
}
