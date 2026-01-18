package com.utime.memoBom.board.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.mapper.TopicMapper;
import com.utime.memoBom.board.vo.TopicListVo;
import com.utime.memoBom.board.vo.TopicReqVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.mapper.CommonMapper;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class TopicDaoImpl implements TopicDao{

	final TopicMapper topicMapper;
	final CommonMapper common;
	
	@PostConstruct
	private void init() throws Exception{
		
	}


	@Override
	public boolean hasTopic(UserVo user) {
		
		return topicMapper.hasTopic(user);
	}
	
	@Override
	public boolean isEmpty() {
		
		return topicMapper.isEmpty();
	}

	@Override
	public boolean checkSameName(String name) {
		
		return topicMapper.checkSameName(name);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveTopic(UserVo user, TopicReqVo reqVo) throws Exception {
		
		int result;
		
		if( reqVo.getTopicNo() < 0L ) {
			reqVo.setOwnerNo(user.getUserNo());
			result = topicMapper.insertTopic(reqVo);
			result += topicMapper.insertTopicFlow(user.getUserNo(), reqVo.getTopicNo());
		}else {
			result = topicMapper.updateTopic(reqVo);
		}
		
		return result;
	}

	@Override
	public TopicVo loadTopic(String uid) {
		
		return topicMapper.loadTopic(uid, -1L);
	}
	
	@Override
	public TopicVo loadTopic(long topicNo) {
		
		return topicMapper.loadTopic(null, topicNo);
	}
	
	@Override
	public TopicListVo listTopic(UserVo user, int page) {
		
		final TopicListVo result = new TopicListVo();
		
		int pageSize = 5;
		int offset = (page - 1) * pageSize;
		
		result.setFresh( topicMapper.listTopicFresh(user.getUserNo(), pageSize, offset) );
		result.setTrending( topicMapper.listTopicTrending(user.getUserNo(), pageSize, offset) );
		
		return result;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int flow(UserVo user, TopicVo reqVo) throws Exception{
		
		int result = -1;
		
		final long topicNo = topicMapper.selectTopicNoByUid( reqVo.getUid() );
		if( topicNo <= 0L ) {
			return result;
		}
		
		if( topicMapper.isTopicFollowed( user.getUserNo(), topicNo ) ) {
			topicMapper.deleteTopicFlow( user.getUserNo(), topicNo );
			return 1;
		}else {
			topicMapper.insertTopicFlow( user.getUserNo(), topicNo );
		}
		
		return 0;
	}
}
