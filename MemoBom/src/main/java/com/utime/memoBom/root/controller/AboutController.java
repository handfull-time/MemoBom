package com.utime.memoBom.root.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("About")
public class AboutController {
	
	@GetMapping(path = {"", "/", "index.html" })
    public String aboutPage() {
		return "About/About";
    }
	
	@GetMapping(path = "Opensource.html")
    public String opensource() {
		return "About/Opensource";
    }
	
	@GetMapping(path = "License.html")
    public String License() {
		return "About/License";
    }
}