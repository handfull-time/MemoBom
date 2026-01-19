package com.utime.memoBom.board.service;

import com.utime.memoBom.board.vo.TopicListVo;
import com.utime.memoBom.board.vo.TopicReqVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;

public interface TopicService {

	/**
	 * 팔로우 한 topic이 있나?
	 * @param user
	 * @return true:있다. flase:없다.
	 */
	boolean hasTopic(UserVo user);

	/**
	 * 동일 이름 있는지 검사.
	 * @param name
	 * @return
	 */
	ReturnBasic checkSameName(String name);

	/**
	 * topic 저장
	 * @param user
	 * @param reqVo
	 * @return
	 */
	ReturnBasic saveTopic(UserVo user, TopicReqVo reqVo);

	/**
	 * topic 읽기
	 * @param uid
	 * @return
	 */
	TopicVo loadTopic(String uid);

	/**
	 * topic 목록
	 * @param user
	 * @return
	 */
	TopicListVo listTopic(UserVo user, int page, String keyword );

	/**
	 * Topic이 하나도 없나?
	 * @return
	 */
	boolean isEmpty();

	/**
	 * 토픽 흐름 저장
	 * @param user
	 * @param reqVo
	 * @return
	 */
	ReturnBasic flow(UserVo user, TopicVo reqVo);


}
