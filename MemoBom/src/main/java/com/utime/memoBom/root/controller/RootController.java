package com.utime.memoBom.root.controller;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping
public class RootController {
	
	@EventListener(ApplicationReadyEvent.class)
	protected void startListening(ApplicationReadyEvent event) {
		log.info("\n\n\n --- Start Application ---\n\n\n");
	}
	
	/**
	 * 로그인 화면
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(path = {"", "/", "index.html" })
    public String rootPage( HttpServletRequest request, ModelMap model, UserVo user ) {
		
		if( user == null ) {
			return "redirect:/Auth/Login.html";
		}else {
			return "redirect:/Fragment/index.html";
		}
    }

	
}

