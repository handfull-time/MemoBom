package com.utime.memoBom.root.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.common.jwt.JwtProvider;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.EJwtRole;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("Test")
@RequiredArgsConstructor
public class TestController {
	
	private final TopicService topicService;
	
    private final JwtProvider jwtProvider;

    private final UserDao userDao;
	
    @GetMapping("Login")
	public String testLogin() throws Exception {
    	return "Test/TestLogin";
    }
    
    @GetMapping("LoginGo")
	public String testLoginGo(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam() String id) throws Exception {

    	UserVo userVo = userDao.findById("localPc", id);
    	
    	if( userVo == null ) {
    		userVo = new UserVo();
    		userVo.setId(id);
    		userVo.setProvider("localPc");
    		userVo.setEnabled(true);
    		userVo.setEmail(id + "@Gmail.cococo" );
    		userVo.setRole(EJwtRole.User);
    		userVo.setNickname( "Tester-" + id );
    		userVo.setProfileUrl( "/MemoBom/images/profile-placeholder.svg" );
    		
    		log.info("사용자 추가 정보 : {}", userVo);
    		
    		userDao.addUser(userVo);
    	}
		
		try {
			ReturnBasic result = jwtProvider.procLogin(request, response, userVo);
			log.info(result.toString());
		} catch (Exception e) {
			log.error("", e);
			model.addAttribute("res", new ReturnBasic("E", e.getMessage()) );
			model.addAttribute(AppDefine.KeyShowFooter, false );
			return "Common/ErrorAlert";
		}

		return "redirect:/";
	}

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

	@GetMapping("TestView2")
	public String test2(Model model) {
	    model.addAttribute("showHeader", true);
	    model.addAttribute("showFooter", false);
		return "Test/Test2";
	}

	@GetMapping("Topic")
	public String topic(Model model) {

		model.addAttribute("topic", topicService.loadTopic(null));
		model.addAttribute("user", new UserVo());
		
		return "Topic/TopicItem";
	}
	
}

