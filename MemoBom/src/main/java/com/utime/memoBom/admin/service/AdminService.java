package com.utime.memoBom.admin.service;

import com.utime.memoBom.admin.vo.AdminSearchVo;
import com.utime.memoBom.admin.vo.AdminUserVo;
import com.utime.memoBom.admin.vo.DashboardVo;
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
	 * 대시보드 집계 정보를 조회한다.
	 *
	 * @param date 조회 일자(yyyy-MM-dd)
	 * @return 대시보드 집계 결과
	 */
	public DashboardVo getDashboard(String date);

	/**
	 * 사용자 목록을 조회한다.
	 *
	 * @param user 요청 사용자
	 * @param searchVo 조회 조건
	 * @return 사용자 목록 응답
	 */
	public ReturnBasic getUserList(LoginUser user, AdminSearchVo searchVo);

	/**
	 * 토픽 목록을 조회한다.
	 *
	 * @param user 요청 사용자
	 * @param searchVo 조회 조건
	 * @return 토픽 목록 응답
	 */
	public ReturnBasic getTopicList(LoginUser user, AdminSearchVo searchVo);

	/**
	 * 글(프래그먼트) 목록을 조회한다.
	 *
	 * @param user 요청 사용자
	 * @param searchVo 조회 조건
	 * @return 글 목록 응답
	 */
	public ReturnBasic getFragmentList(LoginUser user, AdminSearchVo searchVo);

	/**
	 * 댓글 목록을 조회한다.
	 *
	 * @param user 요청 사용자
	 * @param searchVo 조회 조건
	 * @return 댓글 목록 응답
	 */
	public ReturnBasic getCommentList(LoginUser user, AdminSearchVo searchVo);

	/**
	 * 이모션 목록을 조회한다.
	 *
	 * @param user 요청 사용자
	 * @param searchVo 조회 조건
	 * @return 이모션 목록 응답
	 */
	public ReturnBasic getEmotionList(LoginUser user, AdminSearchVo searchVo);

	/**
	 * 스크랩 목록을 조회한다.
	 *
	 * @param user 요청 사용자
	 * @param searchVo 조회 조건
	 * @return 스크랩 목록 응답
	 */
	public ReturnBasic getScrapList(LoginUser user, AdminSearchVo searchVo);

	/**
	 * 공유 목록을 조회한다.
	 *
	 * @param user 요청 사용자
	 * @param searchVo 조회 조건
	 * @return 공유 목록 응답
	 */
	public ReturnBasic getShareList(LoginUser user, AdminSearchVo searchVo);

	/**
	 * 푸시 목록을 조회한다.
	 *
	 * @param user 요청 사용자
	 * @param searchVo 조회 조건
	 * @return 푸시 목록 응답
	 */
	public ReturnBasic getPushList(LoginUser user, AdminSearchVo searchVo);

	/**
	 * 사용자 메모(note)를 저장한다.
	 *
	 * @param user 요청 사용자
	 * @param userVo 사용자 정보(userNo, note)
	 * @return 처리 결과
	 */
	public ReturnBasic updateUserNote(LoginUser user, AdminUserVo userVo);

	/**
	 * 사용자를 삭제한다.
	 *
	 * @param user 요청 사용자
	 * @param userVo 사용자 정보(userNo)
	 * @return 처리 결과
	 */
	public ReturnBasic deleteUser(LoginUser user, AdminUserVo userVo);

}
