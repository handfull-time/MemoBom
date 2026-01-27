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
		
		if( !common.existTable("MB_USER") ) {
			log.info("MB_USER 생성");
			result += mapper.createUser();
			
			result += common.createUniqueIndex("MB_USER_UID_INDX", "MB_USER", "UID");
			result += common.createUniqueIndex("MB_USER_UID_INDX", "MB_USER", "ID");
		}
		
		if( !common.existTable("MB_USER_LOGIN_RECORD") ) {
			log.info("MB_USER_LOGIN_RECORD 생성");
			result += mapper.createLoginRecord();
			
			result += common.createIndex("MB_USER_LOGIN_RECORD_IP_INDX", "MB_USER_LOGIN_RECORD", "IP");
			result += common.createIndex("MB_USER_LOGIN_RECORD_USER_NO_INDX", "MB_USER_LOGIN_RECORD", "USER_NO");
			result += common.createIndex("MB_USER_LOGIN_RECORD_REG_DATE_INDX", "MB_USER_LOGIN_RECORD", "REG_DATE");
		}
		
		if( !common.existTable("MB_TOPIC") ) {
			log.info("MB_TOPIC 생성");
			result +=mapper.createTopic();

			result += common.createUniqueIndex("MB_TOPIC_UID_INDX", "MB_TOPIC", "UID");
			result += common.createUniqueIndex("MB_TOPIC_NAME_UPPER_INDX", "MB_TOPIC", "NAME_UPPER");
		}

		if( !common.existTable("MB_TOPIC_FOLLOW") ) {
			log.info("MB_TOPIC_FOLLOW 생성");
			result += mapper.createTopicFlow();
			
			result += common.createIndex("MB_TOPIC_FOLLOW_USER_NO_INDX", "MB_TOPIC_FOLLOW", "USER_NO");
			result += common.createIndex("MB_TOPIC_FOLLOW_TOPIC_NO_INDX", "MB_TOPIC_FOLLOW", "TOPIC_NO");
		}

		if( !common.existTable("MB_TOPIC_STATS") ) {
			log.info("MB_TOPIC_STATS 생성");
			result += mapper.createTopicStats();
			
			result += common.createIndex("IDX_TOPIC_STATS_TRENDING_INDX", "MB_TOPIC_STATS", "FOLLOW_COUNT DESC, FRAGMENT_COUNT DESC, TOPIC_NO DESC");
		}

		if( !common.existTable("MB_FRAGMENT") ) {
			log.info("MB_FRAGMENT 생성");
			result += mapper.createFragment();

			result += common.createUniqueIndex("MB_FRAGMENT_UID_INDX", "MB_FRAGMENT", "UID");
			result += common.createIndex("MB_FRAGMENT_REG_DATE_INDX", "MB_FRAGMENT", "REG_DATE");
			result += common.createIndex("MB_FRAGMENT_TOPIC_NO_INDX", "MB_FRAGMENT", "TOPIC_NO");
			result += common.createIndex("MB_FRAGMENT_USER_NO_INDX", "MB_FRAGMENT", "USER_NO");
		}

		if( !common.existTable("MB_FRAGMENT_COMMENTS") ) {
			log.info("MB_FRAGMENT_COMMENTS 생성");
			result += mapper.createFragmentComments();
			
			result += common.createIndex("MB_FRAGMENT_COMMENTS_FRAGMENT_NO_INDX", "MB_FRAGMENT_COMMENTS", "FRAGMENT_NO");
			result += common.createIndex("MB_FRAGMENT_COMMENTS_REG_DATE_INDX", "MB_FRAGMENT_COMMENTS", "REG_DATE");
			result += common.createUniqueIndex("MB_FRAGMENT_COMMENTS_UID_INDX", "MB_FRAGMENT_COMMENTS", "UID");
		}

		if( !common.existTable("MB_FRAGMENT_SCRAP") ) {
			log.info("MB_FRAGMENT_SCRAP 생성");
			result += mapper.createFragmentScrap();
			
			result += common.createIndex("MB_FRAGMENT_SCRAP_USER_NO_INDX", "MB_FRAGMENT_SCRAP", "USER_NO");
			result += common.createIndex("MB_FRAGMENT_SCRAP_FRAGMENT_NO_INDX", "MB_FRAGMENT_SCRAP", "FRAGMENT_NO");
		}

		if( !common.existTable("MB_FRAGMENT_EMOTION_LOG") ) {
			log.info("MB_FRAGMENT_EMOTION_LOG 생성");
			result += mapper.createFragmentEmotionLog();
			
			result += common.createIndex("UQ_EMOTION_LOG", "MB_FRAGMENT_EMOTION_LOG", "TARGET_TYPE, TARGET_NO");
		}

		if( !common.existTable("MB_FRAGMENT_HASHTAG") ) {
			log.info("MB_FRAGMENT_HASHTAG 생성");
			result += mapper.createFragmentHashTag();
		}

		if( !common.existTable("MB_FRAGMENT_HASHTAG_RECORD") ) {
			log.info("MB_FRAGMENT_HASHTAG_RECORD 생성");
			result += mapper.createFragmentHashTagRecord();

			result += common.createIndex("IDX_MB_FRAGMENT_HASHTAG_RECORD_FRAGMENT_NO", "MB_FRAGMENT_HASHTAG_RECORD", "FRAGMENT_NO");
			result += common.createIndex("IDX_MB_FRAGMENT_HASHTAG_RECORD_TAG_NO", "MB_FRAGMENT_HASHTAG_RECORD", "TAG_NO");
		}
		
		if( !common.existTable("MB_PUSH_SUB") ) {
			log.info("MB_PUSH_SUB 생성");
			result += mapper.createPushSubscriptionTable();
			
			result += common.createIndex("MB_PUSH_SUB_USER_NO_INDX", "MB_PUSH_SUB", "USER_NO");
			result += common.createUniqueIndex("MB_PUSH_SUB_END_POINT_INDX", "MB_PUSH_SUB", "END_POINT");
		}
		
		if( !common.existTable("MB_SHARE") ) {
			log.info("MB_SHARE 생성");
			result += mapper.createShare();
			
			result += common.createIndex("MB_SHARE_USER_NO_INDX", "MB_SHARE", "USER_NO");
			result += common.createIndex("MB_SHARE_FRAGMENT_NO_INDX", "MB_SHARE", "FRAGMENT_NO");
		}
		
		if( !common.existTable("MB_HOLIDAY") ) {
			log.info("MB_HOLIDAY 생성");
			result += mapper.createHoliday();
			
			result += common.createIndex("MB_HOLIDAY_LOC_DATE_INDX", "MB_HOLIDAY", "LOC_DATE");
			result += common.createUniqueIndex("MB_HOLIDAY_LOC_DATE_NAME_INDX", "MB_HOLIDAY", "LOC_DATE, NAME");
		}
		
		log.info("초기 작업 {}", result);
	}
}
