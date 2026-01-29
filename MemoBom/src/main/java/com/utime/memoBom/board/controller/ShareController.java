package com.utime.memoBom.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.utime.memoBom.board.service.BoardService;
import com.utime.memoBom.board.vo.ShareVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 공유하기
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ShareController {

	final BoardService boardServce;
	final UserService userServce;
	
	@GetMapping("Share/{uid}")
	public String getShareInfoView(ModelMap model, LoginUser user, @PathVariable() String uid) {
		try {
			final ShareVo vo = boardServce.loadShareInfo(user, uid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/Share/FragmentShare";
	}
	
}
