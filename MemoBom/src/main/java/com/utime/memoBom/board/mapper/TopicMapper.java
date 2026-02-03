package com.utime.memoBom.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.board.vo.ETopicSortType;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.board.vo.query.TopicResultVo;
import com.utime.memoBom.common.security.LoginUser;

/**
 * 주제 처리
 */
@Mapper
public interface TopicMapper {
	
	/**
	 * 팔로우 한 topic이 있나?
	 * @param user
	 * @return true:있다. flase:없다.
	 */
	boolean hasTopic(LoginUser user);
	
	/**
	 * 동일 이름 있는지 검사.
	 * @param uid 
	 * @param name
	 * @return
	 */
	boolean checkSameName(@Param("uid") String uid, @Param("name") String name);

	/**
	 * topic 저장
	 * @param reqVo
	 * @return
	 */
	int insertTopic(TopicVo reqVo);
	
	/**
	 * topic 수정
	 * @param reqVo
	 * @return
	 */
	int updateTopic(TopicVo reqVo);

	/**
	 * topic 읽기
	 * @param uid
	 * @return
	 */
	TopicVo loadTopic(@Param("uid") String uid, @Param("topicNo") long no);

	/**
	 * topic 목록
	 * @param userNo
	 * @param keyword
	 * @param pageSize
	 * @param offset
	 * @param sortType 인기 목록 : 'trending', 최신 목록: 'fresh'
	 * @return
	 */
	List<TopicResultVo> listTopic( @Param("user") LoginUser user, @Param("keyword") String keyword, @Param("page") int page, @Param("sortType") ETopicSortType sortType);

	/**
	 * Topic이 하나도 없나?
	 * @return
	 */
	boolean isEmpty();
	
	/**
	 * topic 번호 조회
	 * @param uid
	 * @return
	 */
	long selectTopicNoByUid(@Param("uid") String uid);
	
	/**
	 * topic 팔로우 저장
	 * @param userNo
	 * @param no
	 * @return
	 */
	int insertTopicFlow( @Param("userNo") long userNo, @Param("topicNo") long no );
	
	/**
	 * topic 팔로우 삭제
	 * @param userNo
	 * @param no
	 * @return
	 */
	int deleteTopicFlow( @Param("userNo") long userNo, @Param("topicNo") long no );
	
	/**
	 * topic 팔로우 여부
	 * @param userNo
	 * @param topicNo
	 * @return
	 */
	boolean isTopicFollowed(@Param("userNo") long userNo, @Param("topicNo") long no );

	/**
	 * 사용자의 보유 Topic 목록
	 * @param user
	 * @return
	 */
	List<TopicVo> loadUserTopicList(@Param("userNo") long userNor);
	
	/**
	 * topic 통계 정보 생성
	 * @param topicNo
	 * @return
	 */
	int insertTopicStats( @Param("topicNo") long topicNo );
	
	/**
	 * topic 팔로우 수 갱신
	 * @param topicNo
	 * @return
	 */
	int updateTopicStatsFollowCount( @Param("topicNo") long topicNo);
	
	/**
	 * topic 팔로우 수 감소
	 * @param topicNo
	 * @return
	 */
	int decreaseTopicStatsFollowCount( @Param("topicNo") long topicNo);
	
	/**
	 * 글 작성 성공 후 +1, 최신글 시간 갱신
	 * @param topicNo
	 * @return
	 */
	int increaseTopicStatsFragmentCount( @Param("topicNo") long topicNo);
	
	/**
	 * 글 삭제 성공 후 -1
	 * @param topicNo
	 * @return
	 */
	int decreaseTopicStatsFragmentCount( @Param("topicNo") long topicNo);

	/**
	 * 토픽 조회
	 * @param user
	 * @param topicUid
	 * @return
	 */
	TopicVo loadTopicFromUid(@Param("user") LoginUser user, @Param("uid")String topicUid);
	
	
	/**
	 * 내가 작성하거나 팔로우 한 topic 목록
	 * @param user
	 * @param keyword
	 * @param pageNo
	 * @param uid
	 * @return
	 */
	List<TopicVo> listMyOrFollowTopic(@Param("user") LoginUser user, @Param("keyword")String keyword, @Param("pageNo")int pageNo);

	/**
	 * 공유 데이터 추가.
	 * @param userNo
	 * @param topicNo
	 * @return
	 */
	int insertTopicShareInfo(@Param("userNo") long userNo, @Param("topicNo") long topicNo);
}
