package com.utime.memoBom.board.dao;

import com.utime.memoBom.board.vo.EShareTargetType;
import com.utime.memoBom.board.vo.ShareVo;
import com.utime.memoBom.common.security.LoginUser;

public interface ShareDao {

	/**
	 * 쉐어 정보 추가
	 * @param user
	 * @param targetType
	 * @param uid
	 * @return
	 */
	ShareVo addShareInfo(LoginUser user, EShareTargetType targetType, String uid)throws Exception;
}
