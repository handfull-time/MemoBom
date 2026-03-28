package com.utime.memoBom.admin.dao;

import java.util.List;

import com.utime.memoBom.admin.vo.AdminCommentVo;
import com.utime.memoBom.admin.vo.AdminEmotionVo;
import com.utime.memoBom.admin.vo.AdminFragmentVo;
import com.utime.memoBom.admin.vo.AdminPushVo;
import com.utime.memoBom.admin.vo.AdminScrapVo;
import com.utime.memoBom.admin.vo.AdminSearchVo;
import com.utime.memoBom.admin.vo.AdminShareVo;
import com.utime.memoBom.admin.vo.AdminTopicFollowVo;
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
	 * 토픽을 팔로우한 사용자 목록을 조회한다.
	 *
	 * @param topicNo 토픽 번호
	 * @return 팔로우 사용자 목록
	 */
	List<AdminTopicFollowVo> getTopicFollowUsers(Long topicNo);

	/**
	 * 토픽 수정 가능 항목(ENABLED, MAX_LEN, AI, PROMPT)을 저장한다.
	 *
	 * @param topicVo 수정 대상 값
	 * @return 반영 건수
	 */
	int updateTopicEditable(AdminTopicVo topicVo)throws Exception;

	/**
	 * 글(프래그먼트) 목록을 조회한다.
	 *
	 * @param searchVo 조회 조건
	 * @return 글 목록
	 */
	List<AdminFragmentVo> getFragmentList(AdminSearchVo searchVo);

	/**
	 * 프래그먼트 미리보기 데이터를 조회한다.
	 *
	 * @param fragmentNo 프래그먼트 번호
	 * @return 프래그먼트 정보
	 */
	AdminFragmentVo getFragmentPreview(Long fragmentNo);

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

	/**
	 * 사용자 메모(note)를 저장한다.
	 *
	 * @param userNo 사용자 번호
	 * @param note 메모
	 * @return 반영 건수
	 */
	int updateUserNote(Long userNo, String note)throws Exception;

	/**
	 * 사용자 활성화(enabled) 값을 변경한다.
	 *
	 * @param userNo 사용자 번호
	 * @param enabled 활성화 여부
	 * @return 반영 건수
	 */
	int updateUserEnabled(Long userNo, Boolean enabled)throws Exception;

	/**
	 * 댓글 삭제 여부(IS_DELETED)를 변경한다.
	 *
	 * @param commentNo 댓글 번호
	 * @param deleted 삭제 여부
	 * @return 반영 건수
	 */
	int updateCommentDeleted(Long commentNo, Boolean deleted)throws Exception;

	int deleteInactivePushSubs() throws Exception;

	int deleteComments() throws Exception;

	int deleteFragments() throws Exception;

	int deleteDisabledTopics() throws Exception;

	int deleteDisabledUsers() throws Exception;

}
