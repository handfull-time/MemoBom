package com.utime.memoBom.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("My")
@RequiredArgsConstructor
public class MyController {

	/**
	 * 로그인 화면
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(path = {"", "/", "index.html" })
    public String boardMain( HttpServletRequest request, ModelMap model, UserVo user ) {
		
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
}
