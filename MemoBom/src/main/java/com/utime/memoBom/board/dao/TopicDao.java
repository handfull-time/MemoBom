package com.utime.memoBom.board.dao;

import java.util.List;

import com.utime.memoBom.board.vo.ETopicSortType;
import com.utime.memoBom.board.vo.TopicReqVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.user.vo.UserVo;

public interface TopicDao {
	
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
	boolean checkSameName(String name);

	/**
	 * topic 저장
	 * @param user
	 * @param reqVo
	 * @return
	 */
	int saveTopic(UserVo user, TopicReqVo reqVo) throws Exception;

	/**
	 * topic 읽기
	 * @param uid
	 * @return
	 */
	TopicVo loadTopic(String uid);
	
	/**
	 * topic 읽기
	 * @param topicNo
	 * @return
	 */
	TopicVo loadTopic(long topicNo);

	/**
	 * topic 목록
	 * @param user
	 * @param page 1부터 시작
	 * @return
	 */
	List<TopicVo> listTopic(UserVo user, ETopicSortType sortType, int page, String keyword );

	/**
	 * Topic이 하나도 없나?
	 * @return
	 */
	boolean isEmpty();

	/**
	 * 토픽 flow 저장
	 * @param user
	 * @param reqVo
	 * @return
	 */
	int flow(UserVo user, TopicVo reqVo) throws Exception;
	
	/**
	 * 사용자의 보유 Topic 목록
	 * @param user
	 * @return
	 */
	List<TopicVo> loadUserTopicList(UserVo user);

}
