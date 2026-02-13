package com.utime.memoBom.board.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.board.dto.ShareDto;
import com.utime.memoBom.board.service.ShareService;
import com.utime.memoBom.board.vo.EShareTargetType;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.ReturnBasic;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 공유하기
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ShareController {

	final ShareService shareServce;
	
	@Value("${appName}")
	private String appName;
	
	@GetMapping("Share/{uid}.html")
	public String getShareInfoView(HttpServletRequest request, ModelMap model, LoginUser user, @PathVariable() String uid) {
		
    	final String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
    	final boolean isBot = AppUtils.isBot(userAgent);
    	log.info("Bot 진입("+isBot+") : " + userAgent);
    	
    	final ShareDto vo = shareServce.loadShareInfo( user, uid, isBot);
    	
    	if( isBot ) {
    		
    		final String baseUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath();
    		vo.setUrl( baseUrl + vo.getUrl());
    		model.addAttribute("item", vo);
    		
    		return "Share/ShowShareBot";
    	}
	    		
		return "redirect:" + vo.getUrl();
	}
	
	/**
	 * Topic 공유 정보 생성
	 * @param request
	 * @param user
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@GetMapping("TopicShare.json")
	public ReturnBasic getTopicShareInfo(HttpServletRequest request, LoginUser user, @RequestParam() String uid) throws Exception {
		
		return shareServce.makeShareInfo(request, user, EShareTargetType.Topic, uid);
	}
	
	/**
	 * Fragment 공유 정보 생성
	 * @param user
	 * @param uid
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@GetMapping("FragmentShare.json")
	public ReturnBasic getShareInfo(HttpServletRequest request, LoginUser user, @RequestParam() String uid) throws Exception {
		
		return shareServce.makeShareInfo(request, user, EShareTargetType.Fragment, uid);
	}
	
}
