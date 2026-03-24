package com.utime.memoBom.board.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.dto.InviteDecisionDto;
import com.utime.memoBom.board.dto.InviteUserDto;
import com.utime.memoBom.board.dto.TopicDto;
import com.utime.memoBom.board.dto.TopicSaveDto;
import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.board.vo.ETopicSortType;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.board.vo.query.TopicResultVo;
import com.utime.memoBom.common.dao.KeyValueDao;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.push.service.PushSendService;
import com.utime.memoBom.push.vo.PushSendDataVo;
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
	final PushSendService pushSendService;
	
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
//	TopicResultVo loadTopic(String uid);
	public ReturnBasic loadTopic(LoginUser user, String uid) {
	
		final ReturnBasic result = new ReturnBasic();
		if( AppUtils.isEmpty(uid) ) {
			result.setCodeMessage("E", "Mosaic ID가 없습니다.");
			return result;
		} 
		
		final TopicVo topic = topicDao.loadTopic(user, uid);
		if( topic == null ) {
			result.setCodeMessage("E", "Mosaic을 찾을 수 없습니다.");
			return result;
		}
		
		result.setData( this.convertTopicToTopicResultVo(topic) );
		return result;
	}
	
	@Override
	public ReturnBasic listTopic(LoginUser user, ETopicSortType sortType, int page, String keyword, String uid) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( topicDao.listTopic( user, sortType, page, keyword, uid ) );
		
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
	
	@Override
	public ReturnBasic inviteUser(LoginUser user, InviteUserDto invite) {
		
		final ReturnBasic result = new ReturnBasic();
		
		try {
			int res = topicDao.addInviteUser(user, invite.getTopicUid(), invite.getUserUid() );
			if( res <= 0 ) {
				result.setCodeMessage("E", "No changes were made.");
				return result;
			}

			final TopicVo topic = topicDao.loadTopic(invite.getTopicUid());
			final PushSendDataVo push = new PushSendDataVo();
			push.setTitle("토픽 초대 알림");
			push.setMessage(topic.getName() + " 토픽에 초대되었습니다.");
			push.setImageUrl("/MemoBom/images/logo/logo_black.svg");
			push.setLinkUrl("/Mosaic/Item.html?uid=" + invite.getTopicUid() + "&invite=true");
			pushSendService.sendPush(new LoginUser(0L, invite.getUserUid(), null), push);
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "An error occurred while saving.");
		}
		
		return result;
	}

	@Override
	public ReturnBasic decideInvite(LoginUser user, InviteDecisionDto dto) {
		final ReturnBasic result = new ReturnBasic();
		if( user == null ) {
			result.setCodeMessage("E", "로그인이 필요합니다.");
			return result;
		}
		if( AppUtils.isEmpty(dto.getTopicUid()) ) {
			result.setCodeMessage("E", "토픽 정보가 없습니다.");
			return result;
		}

		if( !topicDao.hasPendingInvite(user, dto.getTopicUid()) ) {
			result.setCodeMessage("E", "처리할 초대 정보가 없습니다.");
			return result;
		}

		try {
			if( dto.isAccept() ) {
				topicDao.acceptInvite(user, dto.getTopicUid());
			}else {
				topicDao.rejectInvite(user, dto.getTopicUid());
			}
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "초대 처리 중 오류가 발생했습니다.");
		}

		return result;
	}

	@Override
	public boolean hasPendingInvite(LoginUser user, String topicUid) {
		if( user == null || AppUtils.isEmpty(topicUid) ) {
			return false;
		}
		return topicDao.hasPendingInvite(user, topicUid);
	}
	
}
