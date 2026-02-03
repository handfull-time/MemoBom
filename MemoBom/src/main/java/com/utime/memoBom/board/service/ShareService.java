package com.utime.memoBom.board.service;

import com.utime.memoBom.board.vo.EShareTargetType;
import com.utime.memoBom.board.vo.ShareVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.ReturnBasic;

import jakarta.servlet.http.HttpServletRequest;

public interface ShareService {

	ShareVo loadShareInfo(LoginUser user, String uid);

	/**
	 * 공유 정보 생성
	 * @param request
	 * @param user
	 * @param targetType
	 * @param targetUid
	 * @return
	 */
	ReturnBasic makeShareInfo(HttpServletRequest request, LoginUser user, EShareTargetType targetType, String targetUid);

}
