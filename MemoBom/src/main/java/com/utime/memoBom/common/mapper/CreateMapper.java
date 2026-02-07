package com.utime.memoBom.common.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * 최초 필수 테이블 관련 Mapper
 */
@Mapper
public interface CreateMapper {
	
	/**
	 * 회원 테이블 생성
	 * @return
	 */
	int createUser();
	
	/**
	 * 로그인 기록
	 * @return
	 */
	int createLoginRecord();
	
	/**
	 * PushSubscriptionMapper 테이블 생성
	 * @return
	 */
	int createPushSubscriptionTable();
	
	/**
	 * 토픽 테이블 생성
	 * @return
	 */
	int createTopic();
	
	/**
	 * 팔로우 테이블
	 * @return
	 */
	int createTopicFlow();
	
	/**
	 * 편린 보드 생성
	 * @return
	 */
	int createFragment();
	
	/**
	 * 댓글 생성
	 * @return
	 */
	int createFragmentComments();
	
	/**
	 * 스크랩 생성
	 * @return
	 */
	int createFragmentScrap();
	
	/**
	 * 이모티콘 기록 생성
	 * @return
	 */
	int createFragmentEmotionLog();
	
	int createFragmentHashTag();
	
	int createFragmentHashTagRecord();

	/**
	 * 토픽 통계 생성
	 * @return
	 */
	int createTopicStats();

	/**
	 * 토픽 통계 생성
	 * @return
	 */
	int createShare();
	
	/**
	 * 휴일 테이블 생성
	 * @return
	 */
	int createHoliday();
	
	/**
	 * 알람 테이블 생성
	 * @return
	 */
	int createAlarm();
	
	/**
	 * 사용자 프로필 이미지
	 * @return
	 */
	int createUserProfile();

	/**
	 * 피드백 생성
	 * @return
	 */
	int createFeedback();
}