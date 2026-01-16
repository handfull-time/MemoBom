package com.utime.memoBom.board.service.impl;

import org.springframework.stereotype.Service;

import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.user.vo.UserVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class TopicServiceImpl implements TopicService {

	final TopicDao topicDao;

	@Override
	public boolean hasTopic(UserVo user) {
		// TODO Auto-generated method stub
		return false;
	}
}
