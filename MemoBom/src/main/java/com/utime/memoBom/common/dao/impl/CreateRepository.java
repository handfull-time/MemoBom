package com.utime.memoBom.common.dao.impl;

import java.util.List;
import java.util.Map;

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
		}
		
		final List<Map<String, String>> userColumns = common.getColumnInfo("MB_USER");
		final boolean hasPushEnabled = userColumns.stream().anyMatch(item -> {
			final String columnName = item.get("COLUMN_NAME");
			return columnName != null && "PUSH_ENABLED".equalsIgnoreCase(columnName);
		});
		if( !hasPushEnabled ) {
			common.addColumn("MB_USER", "PUSH_ENABLED", "INTEGER", "0", false);
		}
		
		if( !common.existTable("MB_USER_LOGIN_RECORD") ) {
			log.info("MB_USER_LOGIN_RECORD 생성");
			result += mapper.createLoginRecord();
			
			result += common.createIndex("MB_USER_LOGIN_RECORD_IP_INDX", "MB_USER_LOGIN_RECORD", "IP");
			result += common.createIndex("MB_USER_LOGIN_RECORD_USER_NO_INDX", "MB_USER_LOGIN_RECORD", "USER_NO");
			result += common.createIndex("MB_USER_LOGIN_RECORD_CREATED_AT_INDX", "MB_USER_LOGIN_RECORD", "CREATED_AT");
		}
		
		if( !common.existTable("MB_TOPIC") ) {
			log.info("MB_TOPIC 생성");
			result +=mapper.createTopic();

			result += common.createIndex("MB_TOPIC_OWNER_NO_INDX", "MB_TOPIC", "OWNER_NO");
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
			
			result += common.createIndex("MB_TOPIC_STATS_TOPIC_NO_INDX", "MB_TOPIC_STATS", "TOPIC_NO");
			result += common.createIndex("MB_TOPIC_STATS_TRENDING_INDX", "MB_TOPIC_STATS", "FOLLOW_COUNT DESC, FRAGMENT_COUNT DESC, TOPIC_NO DESC");
		}

		if( !common.existTable("MB_FRAGMENT") ) {
			log.info("MB_FRAGMENT 생성");
			result += mapper.createFragment();

			result += common.createIndex("MB_FRAGMENT_CREATED_AT_INDX", "MB_FRAGMENT", "CREATED_AT");
			result += common.createIndex("MB_FRAGMENT_USER_NO_INDX", "MB_FRAGMENT", "USER_NO");
			result += common.createIndex("MB_FRAGMENT_TOPIC_NO_INDX", "MB_FRAGMENT", "TOPIC_NO");
		}

		if( !common.existTable("MB_FRAGMENT_COMMENTS") ) {
			log.info("MB_FRAGMENT_COMMENTS 생성");
			result += mapper.createFragmentComments();
			
			result += common.createIndex("MB_FRAGMENT_COMMENTS_USER_NO_INDX", "MB_FRAGMENT_COMMENTS", "USER_NO");
			result += common.createIndex("MB_FRAGMENT_COMMENTS_FRAGMENT_NO_INDX", "MB_FRAGMENT_COMMENTS", "FRAGMENT_NO");
			result += common.createIndex("MB_FRAGMENT_COMMENTS_CREATED_AT_INDX", "MB_FRAGMENT_COMMENTS", "CREATED_AT");
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
			
			result += common.createIndex("MB_FRAGMENT_EMOTION_LOG_USER_NO_INDX", "MB_FRAGMENT_EMOTION_LOG", "USER_NO");
			result += common.createIndex("MB_FRAGMENT_EMOTION_LOG_TARGET_INDX", "MB_FRAGMENT_EMOTION_LOG", "TARGET_TYPE, TARGET_NO");
		}

		if( !common.existTable("MB_FRAGMENT_HASHTAG") ) {
			log.info("MB_FRAGMENT_HASHTAG 생성");
			result += mapper.createFragmentHashTag();
		}

		if( !common.existTable("MB_FRAGMENT_HASHTAG_RECORD") ) {
			log.info("MB_FRAGMENT_HASHTAG_RECORD 생성");
			result += mapper.createFragmentHashTagRecord();

			result += common.createIndex("MB_FRAGMENT_HASHTAG_RECORD_TAG_NO_INDX", "MB_FRAGMENT_HASHTAG_RECORD", "TAG_NO");
			result += common.createIndex("MB_FRAGMENT_HASHTAG_RECORD_FRAGMENT_NO_INDX", "MB_FRAGMENT_HASHTAG_RECORD", "FRAGMENT_NO");
		}
		
		if( !common.existTable("MB_PUSH_SUB") ) {
			log.info("MB_PUSH_SUB 생성");
			result += mapper.createPushSubscriptionTable();
			
			result += common.createIndex("MB_PUSH_SUB_USER_NO_INDX", "MB_PUSH_SUB", "USER_NO");
		}
		
		if( !common.existTable("MB_SHARE") ) {
			log.info("MB_SHARE 생성");
			result += mapper.createShare();
			
			result += common.createIndex("MB_SHARE_USER_NO_INDX", "MB_SHARE", "USER_NO");
			result += common.createIndex("MB_SHARE_TARGET_TYPE_NO_INDX", "MB_SHARE", "TARGET_TYPE, TARGET_NO");
		}

		if( !common.existTable("MB_HOLIDAY") ) {
			log.info("MB_HOLIDAY 생성");
			result += mapper.createHoliday();
			
			result += common.createIndex("MB_HOLIDAY_LOC_DATE_INDX", "MB_HOLIDAY", "LOC_DATE");
			result += common.createUniqueIndex("MB_HOLIDAY_LOC_DATE_NAME_INDX", "MB_HOLIDAY", "LOC_DATE, NAME");
		}
		
		if( !common.existTable("MB_ALARM") ) {
			log.info("MB_ALARM 생성");
			result += mapper.createAlarm();
			
			result += common.createIndex("MB_ALARM_USER_NO_INDX", "MB_ALARM", "USER_NO");
			result += common.createIndex("MB_ALARM_CREATE_AT_INDX", "MB_ALARM", "CREATE_AT");
		}
		
		if( !common.existTable("MB_USER_PROFILE") ) {
			log.info("MB_USER_PROFILE 생성");
			result += mapper.createUserProfile();
		}
		
		if( !common.existTable("MB_FEEDBACK") ) {
			log.info("MB_FEEDBACK 생성");
			result += mapper.createFeedback();
			
			result += common.createIndex("MB_FEEDBACK_USER_NO_INDX", "MB_FEEDBACK", "USER_NO");	
		}
		
		if( !common.existTable("MB_FRAGMENT_IMAGE") ) {
			log.info("MB_FRAGMENT_IMAGE 생성");
			result += mapper.createFragmentImage();
			
			result += common.createIndex("MB_FRAGMENT_IMAGE_USER_NO_INDX", "MB_FRAGMENT_IMAGE", "USER_NO");	
			result += common.createIndex("MB_FRAGMENT_IMAGE_FRAGMENT_NO_INDX", "MB_FRAGMENT_IMAGE", "FRAGMENT_NO");
		}
		
		if( !common.existTable("MB_TOPIC_INVITE_USER") ) {
			log.info("MB_TOPIC_INVITE_USER 생성");
			result += mapper.createInviteUser();
			
			result += common.createIndex("MB_TOPIC_INVITE_USER_SOURCE_USER_NO_INDX", "MB_TOPIC_INVITE_USER", "SOURCE_USER_NO");
			result += common.createIndex("MB_TOPIC_INVITE_USER_TARGET_USER_NO_INDX", "MB_TOPIC_INVITE_USER", "TARGET_USER_NO");
			result += common.createIndex("MB_TOPIC_INVITE_USER_TOPIC_NO_INDX", "MB_TOPIC_INVITE_USER", "TOPIC_NO");
		}
		
		log.info("초기 작업 {}", result);
	}
}
