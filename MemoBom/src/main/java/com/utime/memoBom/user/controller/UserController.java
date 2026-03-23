package com.utime.memoBom.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("User")
@RequiredArgsConstructor
public class UserController {

	final UserService userService;
	
	/**
	 * 사용자 검색
	 * @param user
	 * @param nickName
	 * @return
	 */
	@GetMapping("SearchInviteUser.json")
	@ResponseBody
    public ReturnBasic searchInviteUser( LoginUser user, @RequestParam String nickName ) {
		return userService.searchInviteUser( user, nickName );
	}
	
}
