package com.utime.memoBom.admin.dao.impl;

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
import com.utime.memoBom.admin.vo.gemini.DashboardVo;

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
		//TODO dashBoardMapper를 이용해 DashboardVo 내용을 완성.
		return new DashboardVo();
	}
}
