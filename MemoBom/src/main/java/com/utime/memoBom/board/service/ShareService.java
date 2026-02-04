package com.utime.memoBom.board.service;

import com.utime.memoBom.board.dto.ShareDto;
import com.utime.memoBom.board.vo.EShareTargetType;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.ReturnBasic;

import jakarta.servlet.http.HttpServletRequest;

public interface ShareService {

	/**
	 * Share 링크 정보
	 * @param user
	 * @param uid
	 * @param isBot
	 * @return
	 */
	ShareDto loadShareInfo(LoginUser user, String uid, boolean isBot);

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
