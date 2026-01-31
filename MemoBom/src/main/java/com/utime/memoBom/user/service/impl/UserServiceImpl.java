package com.utime.memoBom.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.utime.memoBom.board.dao.BoardDao;
import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.dto.MyPageDto;
import com.utime.memoBom.user.dto.MySearchDto;
import com.utime.memoBom.user.dto.MyTopicDto;
import com.utime.memoBom.user.dto.UsageStatisticsDto;
import com.utime.memoBom.user.dto.UserDto;
import com.utime.memoBom.user.dto.UserUpdateDto;
import com.utime.memoBom.user.service.UserService;
import com.utime.memoBom.user.vo.UserVo;
import com.utime.memoBom.user.vo.query.UserProfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService{
	
	final UserDao userDao;
	final TopicDao topicDao;
	final BoardDao boardDao;

	@Override
	public UserVo getUserFromUid(String uid) {

		if( AppUtils.isEmpty(uid))
			return null;
		
		return userDao.getUserFromUid(uid);
	}

	@Override
	public MyPageDto getMyPage(LoginUser user) {
		
		final MyPageDto result = new MyPageDto();
		
		final UserDto userDto = new UserDto();
		final UsageStatisticsDto statistics = new UsageStatisticsDto();
		
		BeanUtils.copyProperties(userDao.getBasicUserFromUserNo(user.userNo()), userDto);
		BeanUtils.copyProperties(userDao.getUserStatisticsRecord(user.userNo()), statistics);
		
		result.setUser( userDto );
		result.setStatistics( statistics );
		
		return result;
	}
	
	@Override
	public ReturnBasic checkUser(LoginUser user, String email) {
		
		final ReturnBasic result = new ReturnBasic();
		
		final UserVo dbUser = userDao.getUserFromUid(user.uid());
		if( dbUser == null ) {
			return result.setCodeMessage("E", "사용자 정보 없습니다.");
		}
		
		if( ! dbUser.getEmail().equals(email) ) {
			result.setCodeMessage("E", "사용자 정보 일치하지 않습니다.");
		}
		
		return result;
	}
	
	@Override
	@CacheEvict(value = AppDefine.KeyUserProfileImage, key = "#user.uid()")
	public ReturnBasic updateMyInfo(LoginUser user, UserUpdateDto data) {
		
		final ReturnBasic result = new ReturnBasic();
		if( ! user.uid().equals(data.getUid())) {
			return result.setCodeMessage("E", "이용자 정보 불일치");
		}
		
		try {
			userDao.updateUserInfo( user, data.getNickname(), data.getProfile());
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", e.getMessage());
		}
		
		return result;
	}
	
	@Override
	public ReturnBasic getMyCalendarDataList(LoginUser user , String date) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( userDao.getMyWriteDataList(user, date) );
		
		return result;
	}

	@Override
	public ReturnBasic getMyAlarmDataList(LoginUser user, MySearchDto searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		return result;
	}

	@Override
	public ReturnBasic getMyFragmentsDataList(LoginUser user, MySearchDto searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		
		return result;
	}
	
	@Override
	public ReturnBasic getMyFragmentsDetail(LoginUser user, String uid) {
		
		final ReturnBasic result = new ReturnBasic();

// 이거 이상한데?? 왜 TOPIC을 처리 했지?		
//		final TopicVo topic = topicDao.loadTopic( uid );
//		if( topic == null ) {
//			return result.setCodeMessage("E", "Mosaic 정보 없습니다.");
//		}
//		
//		final MyTopicDto addItem = new MyTopicDto();
//		BeanUtils.copyProperties(topic, addItem);
//		addItem.setEditable( user.userNo() == topic.getOwnerNo() );
//		
//		result.setData(addItem);
		
		return result;
	}

	@Override
	public ReturnBasic getMyMosaicDataList(LoginUser user, MySearchDto searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		final List<TopicVo> list = topicDao.listMyOrFollowTopic(user, searchVo.getKeyword(), searchVo.getPageNo());
		final List<MyTopicDto> resList = new ArrayList<>();
		for( TopicVo src : list ) {
			final MyTopicDto addItem = new MyTopicDto();
			addItem.setUid(src.getUid());
			addItem.setName(src.getName());
			addItem.setColor(src.getColor());
			addItem.setImogi(src.getImogi());
			addItem.setExternal(src.isExternal());
			addItem.setEditable( user.userNo() == src.getOwnerNo() );
			
			resList.add(addItem);
		}
		
		result.setData(resList);
		
		return result;
	}

	@Override
	public ReturnBasic getMyCommentsDataList(LoginUser user, MySearchDto searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		return result;
	}
	
	@Override
	@Cacheable(value = AppDefine.KeyUserProfileImage, key = "#uid")
	public UserProfile getUserProfile(String uid) {
		
		return userDao.getUserProfile(uid);
	}
}
