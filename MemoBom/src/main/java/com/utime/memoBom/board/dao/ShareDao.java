package com.utime.memoBom.board.dao;

import com.utime.memoBom.board.vo.EShareTargetType;
import com.utime.memoBom.board.vo.ShareVo;
import com.utime.memoBom.common.security.LoginUser;

public interface ShareDao {

	/**
	 * 조회
	 * @param user
	 * @param uid
	 * @param isBot
	 * @return
	 */
	ShareVo loadShareInfo(LoginUser user, String uid, boolean isBot);

	/**
	 * 쉐어 정보 추가
	 * @param user
	 * @param targetType
	 * @param uid
	 * @return
	 */
	ShareVo addShareInfo(LoginUser user, EShareTargetType targetType, String uid)throws Exception;

}
