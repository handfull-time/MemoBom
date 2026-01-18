package com.utime.memoBom.board.service;

import com.utime.memoBom.board.vo.BoardReqVo;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.vo.UserVo;

public interface BoardService {

	Object getTopicBoardListFromTopicUid(UserVo user, String topicUid);

	Object getBoardList(UserVo user);

	Object getTopicBoardListFromUserUid(UserVo user, String userUid);

	/**
	 * Fragment(편린) 저장
	 * @param reqVo
	 * @return
	 */
	ReturnBasic saveFragment(UserVo user, BoardReqVo reqVo);

}
