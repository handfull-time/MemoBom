package com.utime.memoBom.user.dao;

import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.vo.UserVo;

public interface UserDao {

	/**
	 * 사용자 계정 찾기
	 * @param provider
	 * @param email
	 * @return
	 */
	UserVo findByEmail(String provider, String email);
	
	/**
	 * 사용자 추가
	 * @param user
	 * @return
	 * @throws Exception
	 */
	int addUser( UserVo user ) throws Exception;

	/**
	 * 로그인 기록 추가
	 * @param result
	 * @param ip
	 * @param device
	 * @return
	 * @throws Exception
	 */
	int addLoginRecord(UserVo result, String ip, UserDevice device) throws Exception;

	/**
	 * 회원 제거
	 * @param userVo
	 * @return
	 * @throws Exception
	 */
	int deleteUser(UserVo userVo)throws Exception;
}
