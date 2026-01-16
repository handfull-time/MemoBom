package com.utime.memoBom.root.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping
public class TestController {
	
	@GetMapping("Layout")
	public String testLayout() {
		return "Test/TestLayout";
	}

	@GetMapping("TestView")
	public String test() {
		return "User/Test";
	}
	
}

