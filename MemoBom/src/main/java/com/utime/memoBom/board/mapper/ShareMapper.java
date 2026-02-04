package com.utime.memoBom.board.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.board.vo.query.ShareDataVo;
import com.utime.memoBom.board.vo.query.ShareTargetInfo;

/**
 * 게시글 처리
 */
@Mapper
public interface ShareMapper {
	/**
	 * 공유 데이터 추가.
	 * @param userNo
	 * @param topicNo
	 * @return
	 */
	int insertShareData(ShareDataVo vo);
	
	/**
	 * 조회
	 * @param shareNo
	 * @param uid
	 * @return
	 */
	ShareDataVo selectShareData(@Param("shareNo")long shareNo, @Param("uid")String uid);

	/**
	 * 공유 대상 Topic 정보 조회
	 * @param uid
	 * @return
	 */
	ShareTargetInfo loadTopicInfo(@Param("topicNo")long topicNo, @Param("uid")String uid);

	/**
	 * 공유 대상 Fragment 정보 조회
	 * @param uid
	 * @return
	 */
	ShareTargetInfo loadFragmentInfo(@Param("fragmentNo")long fragmentNo, @Param("uid")String uid);
}
