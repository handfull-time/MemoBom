package com.utime.memoBom.board.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.utime.memoBom.user.vo.UserVo;

/**
 * 주제 처리
 */
@Mapper
public interface TopicMapper {
	
	/**
	 * 토픽 테이블 생성
	 * @return
	 */
	int createTopic();
	
	int createTopicFlow();
	
	/**
	 * 팔로우 한 topic이 있나?
	 * @param user
	 * @return true:있다. flase:없다.
	 */
	boolean hasTopic(UserVo user);
}
