package com.utime.memoBom.root.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.common.security.JwtProvider;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.EJwtRole;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.dto.MySearchDto;
import com.utime.memoBom.user.service.UserService;
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
    
    final UserService userService;
	
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
		    model.addAttribute(AppDefine.KeyLoadScript, false );
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
	
	@Value("${korean.dataio.key.SpcdeInfoService}")
	String serviceKey;
	
	@ResponseBody	
	@GetMapping("key")
	public String key(Model model) {
//		https://www.data.go.kr/data/15012690/openapi.do
		
//		기념일 정보 조회
//		/getAnniversaryInfo
//		공휴일 정보 조회
//		/getRestDeInfo
//		국경일 정보조회
//		/getHoliDeInfo
//		24절기 정보 조회
//		/get24DivisionsInfo
//		잡절 정보 조회
//		/getSundryDayInfo
		String url = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo?serviceKey="+serviceKey+"&solYear=2026&numOfRows=100";
		// https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo?serviceKey=__key__&stdt=2026
		return url;
	}
	
	@ResponseBody
	@GetMapping(path = "MyMosaic.json")
    public ReturnBasic myMosaic( ) {
		
		final MySearchDto searchVo = new MySearchDto();
		final LoginUser user = new LoginUser(1L, "ab8595e3-0a68-4132-b6a4-0cae79883ac5", EJwtRole.User);
		
		return userService.getMyMosaicDataList( user, searchVo );
    }
	
	@ResponseBody
	@GetMapping(path = "Fragments.json")
    public ReturnBasic myFragments() {
		
		final MySearchDto searchVo = new MySearchDto();
		final LoginUser user = new LoginUser(1L, "ab8595e3-0a68-4132-b6a4-0cae79883ac5", EJwtRole.User);
		
		return userService.getMyFragmentsDataList( user, searchVo );
    }
	
}

