package com.utime.memoBom.admin.dao;

import java.util.List;

import com.utime.memoBom.admin.vo.AdminCommentVo;
import com.utime.memoBom.admin.vo.AdminEmotionVo;
import com.utime.memoBom.admin.vo.AdminFragmentVo;
import com.utime.memoBom.admin.vo.AdminPushVo;
import com.utime.memoBom.admin.vo.AdminScrapVo;
import com.utime.memoBom.admin.vo.AdminSearchVo;
import com.utime.memoBom.admin.vo.AdminShareVo;
import com.utime.memoBom.admin.vo.AdminTopicVo;
import com.utime.memoBom.admin.vo.AdminUserVo;
import com.utime.memoBom.admin.vo.DashboardVo;

public interface AdminDao {

	/**
	 * 날짜 기준 대시보드 집계 정보를 조회한다.
	 *
	 * @param date 조회 일자(yyyy-MM-dd)
	 * @return 대시보드 집계 결과
	 */
	DashboardVo getDashboard(String date);
	
	/**
	 * 사용자 목록을 조회한다.
	 *
	 * @param searchVo 조회 조건
	 * @return 사용자 목록
	 */
	List<AdminUserVo> getUserList(AdminSearchVo searchVo);

	/**
	 * 토픽 목록을 조회한다.
	 *
	 * @param searchVo 조회 조건
	 * @return 토픽 목록
	 */
	List<AdminTopicVo> getTopicList(AdminSearchVo searchVo);

	/**
	 * 글(프래그먼트) 목록을 조회한다.
	 *
	 * @param searchVo 조회 조건
	 * @return 글 목록
	 */
	List<AdminFragmentVo> getFragmentList(AdminSearchVo searchVo);

	/**
	 * 댓글 목록을 조회한다.
	 *
	 * @param searchVo 조회 조건
	 * @return 댓글 목록
	 */
	List<AdminCommentVo> getCommentList(AdminSearchVo searchVo);

	/**
	 * 이모션 목록을 조회한다.
	 *
	 * @param searchVo 조회 조건
	 * @return 이모션 목록
	 */
	List<AdminEmotionVo> getEmotionList(AdminSearchVo searchVo);

	/**
	 * 스크랩 목록을 조회한다.
	 *
	 * @param searchVo 조회 조건
	 * @return 스크랩 목록
	 */
	List<AdminScrapVo> getScrapList(AdminSearchVo searchVo);

	/**
	 * 공유 목록을 조회한다.
	 *
	 * @param searchVo 조회 조건
	 * @return 공유 목록
	 */
	List<AdminShareVo> getShareList(AdminSearchVo searchVo);

	/**
	 * 푸시 목록을 조회한다.
	 *
	 * @param searchVo 조회 조건
	 * @return 푸시 목록
	 */
	List<AdminPushVo> getPushList(AdminSearchVo searchVo);


}
