package com.utime.memoBom.board.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.mapper.TopicMapper;
import com.utime.memoBom.board.vo.ETopicSortType;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.board.vo.query.TopicResultVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class TopicDaoImpl implements TopicDao{

	final TopicMapper topicMapper;

	@Override
	public boolean hasTopic(LoginUser user) {
		
		return topicMapper.hasTopic(user);
	}
	
	@Override
	public boolean isEmpty() {
		
		return topicMapper.isEmpty();
	}

	@Override
	public boolean checkSameName(String uid, String name) {
		
		return topicMapper.checkSameName(uid, name);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveTopic(TopicVo topic) throws Exception {
		
		int result = 0;
		
		if( AppUtils.isEmpty( topic.getUid() ) ) {
			result += topicMapper.insertTopic(topic);
			result += topicMapper.insertTopicFlow(topic.getOwnerNo(), topic.getTopicNo());
			result += topicMapper.insertTopicStats(topic.getTopicNo());
		}else {
			result += topicMapper.updateTopic(topic);
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
	public List<TopicResultVo> listTopic(LoginUser user, ETopicSortType sortType, int page, String keyword) {
		
		final List<TopicResultVo> result;
		
		if( !AppUtils.isEmpty(keyword) ) {
			keyword = keyword.trim();
		}else {
			keyword = null;
		}
		
		result = topicMapper.listTopic(user, keyword, page, sortType);
		
		return result;
	}
	
	@Override
	public TopicVo loadTopic(LoginUser user, String topicUid) {
		
		return topicMapper.loadTopicFromUid(user, topicUid);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int flow(LoginUser user, String uid) throws Exception{
		
		int result = -1;
		
		final long topicNo = topicMapper.selectTopicNoByUid( uid );
		if( topicNo <= 0L ) {
			return result;
		}
		
		if( topicMapper.isTopicFollowed( user.userNo(), topicNo ) ) {
			result = topicMapper.deleteTopicFlow( user.userNo(), topicNo );
			result += topicMapper.decreaseTopicStatsFollowCount( topicNo );
		}else {
			result = topicMapper.insertTopicFlow( user.userNo(), topicNo );
			result += topicMapper.updateTopicStatsFollowCount( topicNo );
		}
		
		return result;
	}
	
	@Override
	public List<TopicVo> loadUserTopicList(LoginUser user) {
		
		return topicMapper.loadUserTopicList(user.userNo());
	}
}
