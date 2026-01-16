package com.utime.memoBom.user.service;

import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.vo.ReqUniqueVo;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

	/**
	 * 초기 정보 - 암호화, 유니크 검사 필수 값 등.
	 * @param request
	 * @return
	 */
	ReqUniqueVo getNewGenUnique(HttpServletRequest request);
	
	/**
	 * 회원 로그 아웃
	 * @param request
	 * @param response
	 * @param user
	 * @return
	 */
	ReturnBasic logout(HttpServletRequest request, HttpServletResponse response, UserVo user);

}
