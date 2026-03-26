package com.utime.memoBom.admin.service;

import com.utime.memoBom.admin.vo.gemini.DashboardVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.ReturnBasic;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AdminService {

	/**
	 * 어드민 로그인 처리
	 * @param request
	 * @param response
	 * @param user
	 * @return
	 */
	public ReturnBasic adminLogin( HttpServletRequest request, HttpServletResponse response, LoginUser user )throws Exception;
	
	/**
	 * 어드민 로그아웃 처리
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ReturnBasic adminLogout( HttpServletRequest request, HttpServletResponse response)throws Exception;

	/**
	 * 대쉬보드 조회
	 * @param date
	 * @return
	 */
	public DashboardVo getDashboard(String date);
}
