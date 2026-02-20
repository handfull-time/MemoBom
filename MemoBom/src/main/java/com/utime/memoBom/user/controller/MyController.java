package com.utime.memoBom.user.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.dto.MySearchDto;
import com.utime.memoBom.user.dto.UserUpdateDto;
import com.utime.memoBom.user.service.AuthService;
import com.utime.memoBom.user.service.UserService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("My")
@RequiredArgsConstructor
public class MyController {

	final UserService userService;
	final AuthService authService;

	@Value("${env.admin.userNo}")
	private String adminUserNo;
	
	private Set<Long> adminUserNoSet;
	
	@PostConstruct
	private void init() {

		this.adminUserNoSet = Arrays.stream(adminUserNo.split(","))
		        .map(String::trim)
		        .filter(s -> s.matches("\\d+"))
		        .map(Long::valueOf)
		        .collect(Collectors.toSet());
	}
	
	/**
	 * MyPage 화면
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(path = {"", "/", "index.html" })
    public String myMain( Model model, LoginUser user ) {
		
		if( user == null ) {
			return "redirect:/About/Application.html";
		}else {
			model.addAttribute("item", userService.getMyPage(user));
			model.addAttribute("isAdmin", adminUserNoSet.contains(user.userNo()) );
			
			return "My/MyPage";
		}
	}
	
	/**
	 * 개인 정보 수정
	 * @param user
	 * @param data
	 * @return
	 */
	@ResponseBody
	@PostMapping("updateUser.json")
	public ReturnBasic updateMyInfo( LoginUser user, UserUpdateDto data ) {
		
		return userService.updateMyInfo( user, data );
	}
	
	/**
	 * 글자 크기 수정
	 * @param user
	 * @param data
	 * @return
	 */
	@ResponseBody
	@PostMapping("updateUserFontSize.json")
	public ReturnBasic updateMyFontSize( LoginUser user, UserUpdateDto data ) {
		
		return userService.updateMyInfoFontSize( user, data );
	}
	
	/**
	 * 로그아웃
	 * @param request
	 * @param response
	 * @param user
	 * @return
	 */
	@ResponseBody
	@PostMapping("Logout.json")
    public ReturnBasic logout( HttpServletRequest request, HttpServletResponse response, LoginUser user) {
		
	    new SecurityContextLogoutHandler().logout(request, response,
	            SecurityContextHolder.getContext().getAuthentication());

	    SecurityContextHolder.clearContext();
	    
        return authService.logout(request, response, user);
    }
	
	/**
	 * <P>회원탈퇴<P/>
	 * 구글 재 로그인 후 SimpleUrlAuthenticationSuccessHandler 에서 새로운 구글 토큰을 이용해 탈퇴 처리 한다.
	 * @param request
	 * @param response
	 * @param user
	 * @param email
	 * @throws IOException
	 */
	@GetMapping("Withdraw/Google")
	public void startWithdrawGoogle(HttpServletRequest request, HttpServletResponse response, LoginUser user, @RequestParam String email) throws IOException {
		
		ReturnBasic check = userService.checkUser( user, email );
		if( check.isError() ) {
			response.sendRedirect(request.getContextPath() + "/Error?message="+check.getMessage());
			return;
		}
		
	    final Cookie cookie = new Cookie(AppDefine.WithdrawMode, "true");
	    cookie.setPath("/");
	    cookie.setMaxAge(300); // 300초
	    response.addCookie(cookie);

	    // 2. 구글 로그인 페이지로 강제 이동 (재로그인 유도)
	    response.sendRedirect(request.getContextPath() + "/oauth2/authorization/google");
	}

	
	@GetMapping(path = "Alarm.html")
    public String myAlarm(Model model) {
		return "My/MyAlarm";
    }
	
	@ResponseBody
	@GetMapping(path = "Alarm.json")
    public ReturnBasic myAlarm( LoginUser user, MySearchDto searchVo ) {
		
		return userService.getMyAlarmDataList( user, searchVo );
    }

	@GetMapping(path = "Mosaic.html")
    public String myMosaic(Model model) {
		return "My/MyMosaic";
    }
	
	@ResponseBody
	@GetMapping(path = "MyMosaic.json")
    public ReturnBasic myMosaic( LoginUser user, MySearchDto searchVo ) {
		
		return userService.getMyMosaicDataList( user, searchVo );
    }
	
	@GetMapping(path = "Fragments.html")
    public String myFragments(Model model ) {
		return "My/MyFragments";
    }
	
	/**
	 * Fragment 목록 보기
	 * @param user
	 * @param searchVo
	 * @return
	 */
	@ResponseBody
	@GetMapping(path = "Fragments.json")
    public ReturnBasic myFragments( LoginUser user, MySearchDto searchVo ) {
		
		return userService.getMyFragmentsDataList( user, searchVo );
    }

	@GetMapping(path = "Comments.html")
    public String myComments(Model model) {
		return "My/MyComments";
    }
	
	@ResponseBody
	@GetMapping(path = "Comments.json")
    public ReturnBasic myComments( LoginUser user, MySearchDto searchVo ) {
		
		return userService.getMyCommentsDataList( user, searchVo );
    }

	@GetMapping(path = "Scrap.html")
    public String myScrap(Model model) {
		return "My/MyScrap";
    }
	
	@ResponseBody
	@GetMapping(path = "Scrap.json")
    public ReturnBasic myScrap( LoginUser user, MySearchDto searchVo ) {
		
		return userService.getMyScrapDataList( user, searchVo );
    }

	@GetMapping(path = "Calendar.html")
    public String myCalendar( Model model) {
		return "My/MyCalendar";
    }
	
	@ResponseBody
	@GetMapping(path = "MyCalendar.json")
    public ReturnBasic myCalendar( LoginUser user, @RequestParam String date ) {
		
		return userService.getMyCalendarDataList( user, date );
    }
	
	
}
