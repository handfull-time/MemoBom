package com.utime.memoBom.root.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.user.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("Test")
@RequiredArgsConstructor
public class TestController {
	
	private final TopicService topicService;
	
	@GetMapping("Layout")
	public String testLayout() {
		return "Test/TestLayout";
	}

	@GetMapping("TestView")
	public String test(Model model) {
	    model.addAttribute("showHeader", true);
	    model.addAttribute("showFooter", false);
		return "Test/Test";
	}
	@GetMapping("Topic")
	public String topic(Model model) {

		model.addAttribute("topic", topicService.loadTopic(null));
		
		return "Topic/TopicMain";
	}
	
}

