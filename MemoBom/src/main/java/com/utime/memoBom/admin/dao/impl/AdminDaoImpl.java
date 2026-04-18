package com.utime.memoBom.admin.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.memoBom.admin.dao.AdminDao;
import com.utime.memoBom.admin.mapper.AdminCommentMapper;
import com.utime.memoBom.admin.mapper.AdminDashBoardMapper;
import com.utime.memoBom.admin.mapper.AdminEmotionMapper;
import com.utime.memoBom.admin.mapper.AdminFragmentMapper;
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
	
	private final AdminCommentMapper commentMapper;
	private final AdminDashBoardMapper dashBoardMapper;
	private final AdminEmotionMapper emotionMapper;
	private final AdminFragmentMapper fragmentMapper;
	private final AdminPushMapper pushMapper;
	private final AdminScrapMapper scrapMapper;
	private final AdminShareMapper shareMapper;
	private final AdminTopicMapper topicMapper;
	private final AdminUserMapper userMapper;
	
	/**
	 * 삭제 대기 일자
	 */
	private final int deleteLimitDay = 14;

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
	@Transactional(rollbackFor = Exception.class)
	public int updateTopicEditable(AdminTopicVo topicVo) throws Exception{
		return topicMapper.updateTopicEditable(topicVo);
	}

	@Override
	public List<AdminFragmentVo> getFragmentList(AdminSearchVo searchVo) {
		return fragmentMapper.getFragmentList(searchVo);
	}

	@Override
	public AdminFragmentVo getFragmentPreview(Long fragmentNo) {
		return commentMapper.getFragmentPreview(fragmentNo);
	}

	@Override
	public List<AdminCommentVo> getCommentList(AdminSearchVo searchVo) {
		return commentMapper.getCommentList(searchVo);
	}

	@Override
	public List<AdminEmotionVo> getEmotionList(AdminSearchVo searchVo) {
		return emotionMapper.getEmotionList(searchVo);
	}

	@Override
	public List<AdminScrapVo> getScrapList(AdminSearchVo searchVo) {
		return scrapMapper.getScrapList(searchVo);
	}

	@Override
	public List<AdminShareVo> getShareList(AdminSearchVo searchVo) {
		return shareMapper.getShareList(searchVo);
	}

	@Override
	public List<AdminPushVo> getPushList(AdminSearchVo searchVo) {
		return pushMapper.getPushList(searchVo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateUserNote(Long userNo, String note) throws Exception{
		return userMapper.updateUserNote(userNo, note);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateUserEnabled(Long userNo, Boolean enabled) throws Exception{
		return userMapper.updateUserEnabled(userNo, enabled);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateCommentDeleted(Long commentNo, Boolean deleted) throws Exception{
		return commentMapper.updateCommentDeleted(commentNo, deleted);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteInactivePushSubs() throws Exception {
		return pushMapper.deleteInactivePushSubs(deleteLimitDay);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteComments() throws Exception {
		return commentMapper.deleteComments(deleteLimitDay);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteFragments() throws Exception {
		
		int result = 0;
		
		result += fragmentMapper.deleteFragmentsImage(deleteLimitDay);
		result += fragmentMapper.deleteFragments(deleteLimitDay);
		
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteDisabledTopics() throws Exception {
		return topicMapper.deleteDisabledTopics(deleteLimitDay);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteDisabledUsers() throws Exception {
		return userMapper.deleteDisabledUsers(deleteLimitDay);
	}
}
