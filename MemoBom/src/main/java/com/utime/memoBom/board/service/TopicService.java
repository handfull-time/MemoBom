package com.utime.memoBom.board.service;

import com.utime.memoBom.user.vo.UserVo;

public interface TopicService {

	/**
	 * 팔로우 한 topic이 있나?
	 * @param user
	 * @return true:있다. flase:없다.
	 */
	boolean hasTopic(UserVo user);

}
