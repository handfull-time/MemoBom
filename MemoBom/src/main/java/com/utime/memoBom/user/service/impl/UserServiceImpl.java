package com.utime.memoBom.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.utime.memoBom.board.dao.BoardDao;
import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.vo.FragmentItem;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.board.vo.query.MyCommentVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.dto.MyCommentDto;
import com.utime.memoBom.user.dto.MyFragmentDto;
import com.utime.memoBom.user.dto.MyPageDto;
import com.utime.memoBom.user.dto.MySearchDto;
import com.utime.memoBom.user.dto.MyTopicDto;
import com.utime.memoBom.user.dto.UsageStatisticsDto;
import com.utime.memoBom.user.dto.UserDto;
import com.utime.memoBom.user.dto.UserUpdateDto;
import com.utime.memoBom.user.service.UserService;
import com.utime.memoBom.user.vo.UserVo;
import com.utime.memoBom.user.vo.query.BasicUserVo;
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
		
		final BasicUserVo userVo = userDao.getBasicUserFromUserNo(user.userNo());
		final UserDto userDto = new UserDto( userVo.getUid(), userVo.getNickname(), userVo.getProfileUrl(), userVo.getFontSize() );

		final UsageStatisticsDto statistics = new UsageStatisticsDto();
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
	@Cacheable(value = AppDefine.KeyUserProfileImage, key = "#uid")
	public UserProfile getUserProfile(String uid) {
		
		return userDao.getUserProfile(uid);
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
		
		final List<FragmentItem> list = boardDao.listMyFragments( user, searchVo.getKeyword(), searchVo.getPageNo());
		final List<MyFragmentDto> fragList = new ArrayList<>();
		
		for( FragmentItem src : list) {
			final MyFragmentDto target = new MyFragmentDto();
			BeanUtils.copyProperties(src, target);
			BeanUtils.copyProperties(src.getTopic(), target.getTopic());
			fragList.add(target);
		}
		
		result.setData( fragList );
		
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

		final List<MyCommentVo> list = boardDao.listMyComments( user, searchVo.getKeyword(), searchVo.getPageNo());
		final List<MyCommentDto> commentList = new ArrayList<>();
		
		for( MyCommentVo src : list) {
			final MyCommentDto target = new MyCommentDto();
			BeanUtils.copyProperties(src, target);
			
			final MyFragmentDto fragment = target.getFragment();
			fragment.setUid(src.getFragmentUid());
			fragment.setContent(src.getFragmentPreview());
			
			BeanUtils.copyProperties(src.getTopic(), fragment.getTopic());
			
			commentList.add(target);
		}
		
		result.setData( commentList );
		
		return result;
	}
	
	@Override
	public ReturnBasic getMyScrapDataList(LoginUser user, MySearchDto searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		final List<FragmentItem> list = boardDao.listMyScrapFragments( user, searchVo.getKeyword(), searchVo.getPageNo());
		final List<MyFragmentDto> fragList = new ArrayList<>();
		
		for( FragmentItem src : list) {
			final MyFragmentDto target = new MyFragmentDto();
			BeanUtils.copyProperties(src, target);
			BeanUtils.copyProperties(src.getTopic(), target.getTopic());
			fragList.add(target);
		}
		
		result.setData( fragList );
		
		return result;
	}
	
}
