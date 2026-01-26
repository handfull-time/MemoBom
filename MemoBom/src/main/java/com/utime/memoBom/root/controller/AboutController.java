package com.utime.memoBom.root.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("About")
public class AboutController {
	
	@GetMapping(path = {"", "/", "index.html" })
    public String aboutPage() {
		return "My/About";
    }
	
	@GetMapping(path = "Opensource.html")
    public String opensource() {
		return "My/Opensource";
    }
	
}

