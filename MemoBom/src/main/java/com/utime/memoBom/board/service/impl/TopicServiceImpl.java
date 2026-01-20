package com.utime.memoBom.board.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.board.vo.TopicListVo;
import com.utime.memoBom.board.vo.TopicReqVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.dao.KeyValueDao;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class TopicServiceImpl implements TopicService {

	final TopicDao topicDao;
	final KeyValueDao keyValueDao;
	
	@Override
	public String createKey(HttpServletRequest request, UserVo user) {

		return KeyUtil.createKey(keyValueDao, request, user);
	}

	@Override
	public boolean hasTopic(UserVo user) {
		
		return topicDao.hasTopic(user);
	}
	
	@Override
	public boolean isEmpty() {
		return topicDao.isEmpty();
	}
	
	@Override
	public ReturnBasic checkSameName(String name) {
		
		final ReturnBasic result = new ReturnBasic();
		
		if( topicDao.checkSameName(name) ) {
			result.setCodeMessage("E", "There is already a name.");
		}
		
		return result;
	}
	
	@Override
	public ReturnBasic saveTopic(UserVo user, TopicReqVo reqVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		try {
			topicDao.saveTopic(user, reqVo);
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "An error occurred while saving.");
		}
		
		return result;
	}
	
	@Override
	public TopicVo loadTopic(String uid) {
		
		final TopicVo result;
		
		if( AppUtils.isEmpty(uid) ) {
			result = new TopicVo();
		} else {
			result = topicDao.loadTopic(uid);
		}
		
		return result;
	}
	
	@Override
	public TopicListVo listTopic(UserVo user, int page, String keyword) {
		
		return topicDao.listTopic( user, page, keyword );
	}
	
	@Override
	public ReturnBasic flow(UserVo user, TopicVo reqVo) {
	
		final ReturnBasic result = new ReturnBasic();
		
		try {
			int res = topicDao.flow(user, reqVo);
			if( res <= 0 ) {
				result.setCodeMessage("E", "No changes were made.");
			}
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "An error occurred while saving.");
		}
		
		return result;
	}
	
	@Override
	public List<TopicVo> getTopicList(UserVo user) {
		// TODO Auto-generated method stub
		return null;
	}
}
