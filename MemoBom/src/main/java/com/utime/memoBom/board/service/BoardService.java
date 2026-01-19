package com.utime.memoBom.board.service;

import com.utime.memoBom.board.vo.BoardReqVo;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;

public interface BoardService {


	/**
	 * 키 생성
	 * @param request
	 * @return
	 */
	String createKey(HttpServletRequest request);
	
	Object getTopicBoardListFromTopicUid(UserVo user, String topicUid);

	Object getBoardList(UserVo user);

	Object getTopicBoardListFromUserUid(UserVo user, String userUid);

	/**
	 * Fragment(편린) 저장
	 * @param reqVo
	 * @return
	 */
	ReturnBasic saveFragment(UserVo user, UserDevice device, BoardReqVo reqVo);

}
