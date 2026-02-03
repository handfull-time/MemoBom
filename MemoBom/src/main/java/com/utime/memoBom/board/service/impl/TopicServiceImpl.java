package com.utime.memoBom.board.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.dto.TopicDto;
import com.utime.memoBom.board.dto.TopicSaveDto;
import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.board.vo.ETopicSortType;
import com.utime.memoBom.board.vo.ShareVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.board.vo.query.TopicResultVo;
import com.utime.memoBom.common.dao.KeyValueDao;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.dao.UserDao;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class TopicServiceImpl implements TopicService {

	final TopicDao topicDao;
	final UserDao userDao;
	final KeyValueDao keyValueDao;
	
	@Override
	public String createKey(HttpServletRequest request, LoginUser user) {

		return KeyUtil.createKey(keyValueDao, request, user);
	}

	@Override
	public boolean hasTopic(LoginUser user) {
		
		return topicDao.hasTopic(user);
	}
	
	@Override
	public boolean isEmpty() {
		return topicDao.isEmpty();
	}
	
	@Override
	public ReturnBasic checkSameName(String uid, String name) {
		
		final ReturnBasic result = new ReturnBasic();
		
		if( topicDao.checkSameName(uid, name) ) {
			result.setCodeMessage("E", "There is already a name.");
		}
		
		return result;
	}
	
	@Override
	public ReturnBasic saveTopic(LoginUser user, TopicSaveDto reqVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		if( topicDao.checkSameName(reqVo.getUid(),  reqVo.getName()) ) {
			result.setCodeMessage("E", "There is already a name.");
			return result;
		}
		
		final TopicVo topicDb = topicDao.loadTopic(reqVo.getUid());
		if( topicDb != null && topicDb.getOwnerNo() != user.userNo() ) {
			result.setCodeMessage("E", "같은 사용자만 수정 가능 합니다.");
			return result;
		}
		
		final TopicVo topic = new TopicVo();
		BeanUtils.copyProperties(reqVo, topic);
		topic.setOwnerNo( user.userNo() );
		
		try {
			topicDao.saveTopic(topic);
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "An error occurred while saving.");
		}
		
		return result;
	}
	
	/**
	 * TopicVo -> TopicResultVo
	 * @param topic
	 * @return
	 */
	private TopicResultVo convertTopicToTopicResultVo(TopicVo topic) {
		
		final TopicResultVo result = new TopicResultVo();
		BeanUtils.copyProperties(topic, result);
		
		result.setUser( userDao.getBasicUserFromUserNo( topic.getOwnerNo() ) );
		
		return result;
	}
	
	@Override
	public TopicResultVo loadTopic(String uid) {
		
		if( AppUtils.isEmpty(uid) ) {
			return null;
		} 
		
		final TopicVo topic = topicDao.loadTopic(uid);
		if( topic == null ) {
			return null;
		}
		
		return this.convertTopicToTopicResultVo(topic);
	}
	
	@Override
	public ReturnBasic listTopic(LoginUser user, ETopicSortType sortType, int page, String keyword) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( topicDao.listTopic( user, sortType, page, keyword ) );
		
		return result;
	}
	
	@Override
	public ReturnBasic flow(LoginUser user, TopicDto reqVo) {
	
		final ReturnBasic result = new ReturnBasic();
		
		try {
			int res = topicDao.flow(user, reqVo.getUid());
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
	public List<TopicResultVo> loadUserTopicList(LoginUser user) {
		
		final List<TopicResultVo> result = new ArrayList<>();
		
		final List<TopicVo> list = topicDao.loadUserTopicList(user);
		if( AppUtils.isNotEmpty(list)) {
			for( TopicVo topic : list)
			result.add( this.convertTopicToTopicResultVo(topic) );
		}
		
		return result;
	}
	
	@Value("${appName}")
	private String appName;
	
	@Override
	public ShareVo loadShareInfo(LoginUser user, String uid) {
		final ShareVo result = topicDao.addShareInfo(user, uid);
		
		result.setTitle(this.appName + " - Shared");
		
		return result;
	}
}
