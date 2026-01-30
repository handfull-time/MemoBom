package com.utime.memoBom.root.controller;

import java.io.OutputStream;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.utime.memoBom.user.service.UserService;
import com.utime.memoBom.user.vo.query.UserProfile;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
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
    public String rootPage() {
		
		return "redirect:/Fragment/index.html";
    }

	final UserService userService;
	
	@GetMapping("UserImage/{userUid}")
	public void getUserProfileImage(@PathVariable String userUid,
	                                HttpServletResponse response) throws Exception {

	    final UserProfile profile = userService.getUserProfile(userUid);

	    if (profile == null || profile.getImage() == null) {
	        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	        return;
	    }

	    String mimeType = profile.getMimeType();
	    if (mimeType == null || mimeType.isBlank()) {
	        mimeType = "image/jpeg";
	    }

	    response.setContentType(mimeType);
	    response.setContentLength(profile.getImage().length);
	    
	    //브라우저 캐시도 허용
	    response.setHeader("Cache-Control", "public, max-age=300");

	    try (OutputStream os = response.getOutputStream()) {
	        os.write(profile.getImage());
	        os.flush();
	    }
	}

}

