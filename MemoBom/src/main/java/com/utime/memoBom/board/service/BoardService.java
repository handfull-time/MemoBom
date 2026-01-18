package com.utime.memoBom.board.service;

import com.utime.memoBom.user.vo.UserVo;

public interface BoardService {

	Object getTopicBoardListFromTopicUid(UserVo user, String topicUid);

	Object getBoardList(UserVo user);

	Object getTopicBoardListFromUserUid(UserVo user, String userUid);

}
