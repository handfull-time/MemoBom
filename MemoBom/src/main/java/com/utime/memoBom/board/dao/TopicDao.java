package com.utime.memoBom.board.dao;

import java.util.List;

import com.utime.memoBom.board.vo.ETopicSortType;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.board.vo.query.TopicResultVo;
import com.utime.memoBom.common.security.LoginUser;

public interface TopicDao {
	
	/**
	 * 팔로우 한 topic이 있나?
	 * @param user
	 * @return true:있다. flase:없다.
	 */
	boolean hasTopic(LoginUser user);

	/**
	 * 동일 이름 있는지 검사.
	 * @param uid 기존 것은 피하기 위해
	 * @param name
	 * @return
	 */
	boolean checkSameName(String uid, String name);

	/**
	 * topic 저장
	 * @param topic
	 * @return
	 */
	int saveTopic(TopicVo topic) throws Exception;

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
	List<TopicResultVo> listTopic(LoginUser user, ETopicSortType sortType, int page, String keyword, String uid );

	/**
	 * Topic이 하나도 없나?
	 * @return
	 */
	boolean isEmpty();

	/**
	 * 토픽 flow 저장
	 * @param user
	 * @param uid
	 * @return
	 */
	int flow(LoginUser user, String uid) throws Exception;
	
	/**
	 * 사용자의 보유 Topic 목록
	 * @param user
	 * @return
	 */
	List<TopicVo> loadUserTopicList(LoginUser user);

	/**
	 * 토픽 조회
	 * @param user
	 * @param topicUid
	 * @return
	 */
	TopicVo loadTopic(LoginUser user, String topicUid);
	
	/**
	 * 내가 작성하거나 팔로우 한 topic 목록
	 * @param user
	 * @param keyword
	 * @param pageNo
	 * @return
	 */
	List<TopicVo> listMyOrFollowTopic(LoginUser user, String keyword, int pageNo);


}
