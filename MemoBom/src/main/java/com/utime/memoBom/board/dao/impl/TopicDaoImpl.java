package com.utime.memoBom.board.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.mapper.TopicMapper;
import com.utime.memoBom.board.vo.TopicListVo;
import com.utime.memoBom.board.vo.TopicReqVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.user.vo.UserVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class TopicDaoImpl implements TopicDao{

	final TopicMapper topicMapper;

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
		
		int result = 0;
		
		if( AppUtils.isEmpty( reqVo.getUid() ) ) {
			reqVo.setOwnerNo(user.getUserNo());
			result += topicMapper.insertTopic(reqVo);
			result += topicMapper.insertTopicFlow(user.getUserNo(), reqVo.getTopicNo());
			result += topicMapper.insertTopicStats(reqVo.getTopicNo());
		}else {
			result += topicMapper.updateTopic(reqVo);
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
	public TopicListVo listTopic(UserVo user, int page, String keyword) {
		
		final TopicListVo result = new TopicListVo();
		
		final long userNo = user == null ? 0 : user.getUserNo();
		
		if( !AppUtils.isEmpty(keyword) ) {
			keyword = keyword.trim();
			result.setSearch( topicMapper.listTopic(userNo, keyword, page, "new") );
		}else {
			result.setFresh( topicMapper.listTopic(userNo, null, page, "new"));
			result.setTrending( topicMapper.listTopic(userNo, null, page, "trending") );
		}
		
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
			result = topicMapper.deleteTopicFlow( user.getUserNo(), topicNo );
			result += topicMapper.decreaseTopicStatsFollowCount( topicNo );
		}else {
			result = topicMapper.insertTopicFlow( user.getUserNo(), topicNo );
			result += topicMapper.updateTopicStatsFollowCount( topicNo );
		}
		
		return result;
	}
	
	@Override
	public List<TopicVo> loadUserTopicList(UserVo user) {
		
		return topicMapper.loadUserTopicList(user);
	}
}
