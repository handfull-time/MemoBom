package com.utime.memoBom.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.user.vo.UserVo;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("Topic")
@RequiredArgsConstructor
public class TopicController {
	
	final TopicService topicServce;
	
	/**
	 * 로그인 화면
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(path = {"", "/", "index.html" })
    public String topicMain( ModelMap model, UserVo user ) {
		
		if( user == null ) {
			return "redirect:/Auth/Login.html";
		}else {
			return "Topic/TopicMain";
		}
    }

	
}

