package com.utime.memoBom.board.dao.impl;

import org.springframework.stereotype.Repository;

import com.utime.memoBom.board.dao.BoardDao;
import com.utime.memoBom.board.mapper.BoardMapper;
import com.utime.memoBom.board.mapper.TopicMapper;
import com.utime.memoBom.common.mapper.CommonMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class BoardDaoImpl implements BoardDao{

	final BoardMapper boardMapper;
	final TopicMapper topicMapper;
	
	final CommonMapper common;
	
	@PostConstruct
	private void init() throws Exception{
		int result = 0;
		if( ! common.existTable("MB_TOPIC") ) {
			log.info("MB_TOPIC 생성");
			result += topicMapper.createTopic();
			
			result += common.createUniqueIndex("MB_TOPIC_UID_INDX", "MB_TOPIC", "UID");
			result += common.createUniqueIndex("MB_TOPIC_NAME_UPPER_INDX", "MB_TOPIC", "NAME_UPPER");
		}
		
		if( ! common.existTable("MB_TOPIC_FOLLOW") ) {
			log.info("MB_TOPIC_FOLLOW 생성");
			result += topicMapper.createTopicFlow();
		}

		log.info("초기 작업 {}", result);
		
		if( ! common.existTable("MB_MEMO_BOARD") ) {
			log.info("MB_MEMO_BOARD 생성");
			result += boardMapper.createMemoBoard();
			
			//result += common.createIndex("IDX_BOARD_TOPIC", "MB_MEMO_BOARD", "TOPIC_NO");
			//result += common.createIndex("IDX_BOARD_USER", "MB_MEMO_BOARD", "USER_NO");
			result += common.createIndex("IDX_BOARD_REG", "MB_MEMO_BOARD", "REG_DATE");
		}
		
		if( ! common.existTable("MB_MEMO_COMMENTS") ) {
			log.info("MB_MEMO_COMMENTS 생성");
			result += boardMapper.createMemoComments();
		}

		if( ! common.existTable("MB_MEMO_SCRAP") ) {
			log.info("MB_MEMO_SCRAP 생성");
			result += boardMapper.createMemoScrap();
		}

		if( ! common.existTable("MB_MEMO_EMOTION_LOG") ) {
			log.info("MB_MEMO_EMOTION_LOG 생성");
			result += boardMapper.createMemoEmotionLog();
		}

		log.info("초기 작업 {}", result);
	}
}
