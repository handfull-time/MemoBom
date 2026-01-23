package com.utime.memoBom.common.dao.impl;

import org.springframework.stereotype.Repository;

import com.utime.memoBom.common.mapper.CommonMapper;
import com.utime.memoBom.common.mapper.CreateMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class CreateRepository {
	private final CommonMapper common;
	private final CreateMapper mapper;
	
	@PostConstruct
	private void init() throws Exception{
		int result = 0;
		
		log.info("DB 초기 작업 시작");
		
		if( mapper.createUser() > 0 ) {
			log.info("MB_USER 생성");
			result ++;
		}
		
		if( mapper.createLoginRecord() > 0 ) {
			log.info("MB_USER_LOGIN_RECORD 생성");
			result ++;
			result += common.createIndex("MB_USER_LOGIN_RECORD_IP_INDX", "MB_USER_LOGIN_RECORD", "IP");
			result += common.createIndex("MB_USER_LOGIN_RECORD_USER_NO_INDX", "MB_USER_LOGIN_RECORD", "USER_NO");
			result += common.createIndex("MB_USER_LOGIN_RECORD_REG_DATE_INDX", "MB_USER_LOGIN_RECORD", "REG_DATE");
		}
		
		if ( mapper.createTopic() > 0 ) {
			log.info("MB_TOPIC 생성");
			result ++;

			result += common.createUniqueIndex("MB_TOPIC_UID_INDX", "MB_TOPIC", "UID");
			result += common.createUniqueIndex("MB_TOPIC_NAME_UPPER_INDX", "MB_TOPIC", "NAME_UPPER");
		}

		if ( mapper.createTopicFlow() > 0 ) {
			log.info("MB_TOPIC_FOLLOW 생성");
			result ++;
		}

		if( mapper.createTopicStats() > 0 ) {
			log.info("MB_TOPIC_STATS 생성");
			result ++;
			result += common.createIndex("IDX_TOPIC_STATS_TRENDING", "MB_TOPIC_STATS", "FOLLOW_COUNT DESC, FRAGMENT_COUNT DESC, TOPIC_NO DESC");
		}

		if ( mapper.createFragment() > 0 ) {
			log.info("MB_FRAGMENT 생성");
			result ++;

			result += common.createIndex("IDX_BOARD_REG", "MB_FRAGMENT", "REG_DATE");
		}

		if ( mapper.createFragmentComments() > 0 ) {
			log.info("MB_FRAGMENT_COMMENTS 생성");
			result ++;
		}

		if ( mapper.createFragmentScrap() > 0 ) {
			log.info("MB_FRAGMENT_SCRAP 생성");
			result ++;
		}

		if ( mapper.createFragmentEmotionLog() > 0 ) {
			log.info("MB_FRAGMENT_EMOTION_LOG 생성");
			result ++;
		}

		if ( mapper.createFragmentHashTag() > 0 ) {
			log.info("MB_FRAGMENT_HASHTAG 생성");
			result ++;
		}

		if ( mapper.createFragmentHashTagRecord() > 0 ) {
			log.info("MB_FRAGMENT_HASHTAG_RECORD 생성");
			result ++;

			result += common.createIndex("IDX_MB_FRAGMENT_HASHTAG_RECORD_FRAGMENT_NO", "MB_FRAGMENT_HASHTAG_RECORD", "FRAGMENT_NO");
		}
		
		if( mapper.createPushSubscriptionTable() > 0 ) {
			log.info("MB_PUSH_SUB 생성");
			result ++;
			result += common.createIndex("MB_PUSH_SUB_USER_NO_INDX", "MB_PUSH_SUB", "USER_NO");
			result += common.createUniqueIndex("MB_PUSH_SUB_END_POINT_INDX", "MB_PUSH_SUB", "END_POINT");
		}
		
		
		
		log.info("초기 작업 {}", result);
	}
}
