package com.utime.memoBom.admin.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.utime.memoBom.admin.dao.AdminDao;
import com.utime.memoBom.admin.mapper.AdminCommentMapper;
import com.utime.memoBom.admin.mapper.AdminDashBoardMapper;
import com.utime.memoBom.admin.mapper.AdminEmotionMapper;
import com.utime.memoBom.admin.mapper.AdminFragmentMapper;
import com.utime.memoBom.admin.mapper.AdminMapper;
import com.utime.memoBom.admin.mapper.AdminPushMapper;
import com.utime.memoBom.admin.mapper.AdminScrapMapper;
import com.utime.memoBom.admin.mapper.AdminShareMapper;
import com.utime.memoBom.admin.mapper.AdminTopicMapper;
import com.utime.memoBom.admin.mapper.AdminUserMapper;
import com.utime.memoBom.admin.vo.AdminCommentVo;
import com.utime.memoBom.admin.vo.AdminEmotionVo;
import com.utime.memoBom.admin.vo.AdminFragmentVo;
import com.utime.memoBom.admin.vo.AdminPushVo;
import com.utime.memoBom.admin.vo.AdminScrapVo;
import com.utime.memoBom.admin.vo.AdminSearchVo;
import com.utime.memoBom.admin.vo.AdminShareVo;
import com.utime.memoBom.admin.vo.AdminTopicFollowVo;
import com.utime.memoBom.admin.vo.AdminTopicVo;
import com.utime.memoBom.admin.vo.AdminUserVo;
import com.utime.memoBom.admin.vo.DashboardVo;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class AdminDaoImpl implements AdminDao {
	
	private final AdminMapper adminMapper;
	private final AdminCommentMapper commentMapper;
	private final AdminDashBoardMapper dashBoardMapper;
	private final AdminEmotionMapper emotionMapper;
	private final AdminFragmentMapper fragmentMapper;
	private final AdminPushMapper pushMapper;
	private final AdminScrapMapper scrapMapper;
	private final AdminShareMapper shareMapper;
	private final AdminTopicMapper topicMapper;
	private final AdminUserMapper userMapper;

	@Override
	public DashboardVo getDashboard(String date) {
		final DashboardVo item = dashBoardMapper.getDashboard(date);
		return item == null ? new DashboardVo() : item;
	}

	@Override
	public List<AdminUserVo> getUserList(AdminSearchVo searchVo) {
		return userMapper.getUserList(searchVo);
	}

	@Override
	public List<AdminTopicVo> getTopicList(AdminSearchVo searchVo) {
		return topicMapper.getTopicList(searchVo);
	}

	@Override
	public List<AdminTopicFollowVo> getTopicFollowUsers(Long topicNo) {
		return topicMapper.getTopicFollowUsers(topicNo);
	}

	@Override
	public int updateTopicEditable(AdminTopicVo topicVo) {
		return topicMapper.updateTopicEditable(topicVo);
	}

	@Override
	public List<AdminFragmentVo> getFragmentList(AdminSearchVo searchVo) {
		
		return null;
	}

	@Override
	public List<AdminCommentVo> getCommentList(AdminSearchVo searchVo) {
		
		return null;
	}

	@Override
	public List<AdminEmotionVo> getEmotionList(AdminSearchVo searchVo) {
		
		return null;
	}

	@Override
	public List<AdminScrapVo> getScrapList(AdminSearchVo searchVo) {
		
		return null;
	}

	@Override
	public List<AdminShareVo> getShareList(AdminSearchVo searchVo) {
		
		return null;
	}

	@Override
	public List<AdminPushVo> getPushList(AdminSearchVo searchVo) {
		
		return null;
	}

	@Override
	public int updateUserNote(Long userNo, String note) {
		return userMapper.updateUserNote(userNo, note);
	}

	@Override
	public int updateUserEnabled(Long userNo, Boolean enabled) {
		return userMapper.updateUserEnabled(userNo, enabled);
	}
}
