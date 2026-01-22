package com.utime.memoBom.user.service;

import com.utime.memoBom.user.vo.UserVo;

public interface UserService {

	/**
	 * 사용자 정보 조회
	 * @param uid
	 * @return
	 */
	UserVo getUserFromUid(String uid);

}
