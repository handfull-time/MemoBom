package com.utime.memoBom.board.service;

import java.util.List;

import com.utime.memoBom.board.dto.TopicDto;
import com.utime.memoBom.board.dto.TopicSaveDto;
import com.utime.memoBom.board.vo.ETopicSortType;
import com.utime.memoBom.board.vo.query.TopicResultVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.ReturnBasic;

import jakarta.servlet.http.HttpServletRequest;

public interface TopicService {

	/**
	 * 키 생성
	 * @param request
	 * @return
	 */
	String createKey(HttpServletRequest request, LoginUser user);
	
	/**
	 * 팔로우 한 topic이 있나?
	 * @param user
	 * @return true:있다. flase:없다.
	 */
	boolean hasTopic(LoginUser user);

	/**
	 * 동일 이름 있는지 검사.
	 * @param name
	 * @return
	 */
	ReturnBasic checkSameName(String uid, String name);

	/**
	 * topic 저장
	 * @param user
	 * @param reqVo
	 * @return
	 */
	ReturnBasic saveTopic(LoginUser user, TopicSaveDto reqVo);

	/**
	 * topic 읽기
	 * @param uid
	 * @return
	 */
//	TopicResultVo loadTopic(String uid);
	ReturnBasic loadTopic(LoginUser user, String uid);
	
	/**
	 * 사용자의 보유 Topic 목록
	 * @param user
	 * @return
	 */
	List<TopicResultVo> loadUserTopicList(LoginUser user);

	/**
	 * topic 목록
	 * @param user
	 * @param sortType
	 * @param page
	 * @param keyword
	 * @param uid
	 * @return
	 */
	ReturnBasic listTopic(LoginUser user, ETopicSortType sortType, int page, String keyword, String uid );

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
	ReturnBasic flow(LoginUser user, TopicDto reqVo);

}
