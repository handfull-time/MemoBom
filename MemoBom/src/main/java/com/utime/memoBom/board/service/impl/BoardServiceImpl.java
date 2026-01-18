package com.utime.memoBom.board.service.impl;

import org.springframework.stereotype.Service;

import com.utime.memoBom.board.dao.BoardDao;
import com.utime.memoBom.board.service.BoardService;
import com.utime.memoBom.user.vo.UserVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class BoardServiceImpl implements BoardService {

	final BoardDao boardDao;

	@Override
	public Object getTopicBoardListFromTopicUid(UserVo user, String topicUid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getBoardList(UserVo user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getTopicBoardListFromUserUid(UserVo user, String userUid) {
		// TODO Auto-generated method stub
		return null;
	}
}
