package com.utime.memoBom.root.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.utime.memoBom.common.vo.AppDefine;

@Controller
@RequestMapping("About")
public class AboutController {
	
	@GetMapping(path = {"", "/", "index.html" })
    public String aboutPage() {
		return "About/About";
    }
	
	@GetMapping(path = "Opensource.html")
    public String opensource(ModelMap model) {
	    model.addAttribute(AppDefine.KeyShowFooter, false);
	    model.addAttribute(AppDefine.KeyLoadScript, false );
		return "About/Opensource";
    }
	
	@GetMapping(path = "License.html")
    public String License() {
		return "About/License";
    }
	
	@GetMapping(path = "Privacy_Policy.html")
    public String PrivacyPolicy(ModelMap model) {
	    model.addAttribute(AppDefine.KeyShowFooter, false);
	    model.addAttribute(AppDefine.KeyLoadScript, false );
		return "About/Privacy_Policy";
    }
	
	@GetMapping(path = "Terms_of_Service.html")
    public String TermsOfService(ModelMap model) {
	    model.addAttribute(AppDefine.KeyShowFooter, false);
	    model.addAttribute(AppDefine.KeyLoadScript, false );
		return "About/Terms_of_Service";
    }

	@GetMapping(path = "Application.html")
    public String Application() {
		return "About/Application";
    }

}