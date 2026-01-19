package com.utime.memoBom.board.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.board.vo.FragmentVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.vo.UserVo;

/**
 * 게시글 처리
 */
@Mapper
public interface BoardMapper {

	int upsertEmotion(long userNo, int targetType, long targetNo, String emotion);
	
	int deleteEmotion(long userNo, int targetType, long targetNo);
	
	/**
	 * Fragment(편린) 저장
	 * @param reqVo
	 * @return
	 */
	int insertFragment(@Param("user") UserVo user, @Param("device") UserDevice device, @Param("topic") TopicVo topic, @Param("req") FragmentVo reqVo);
	
	int mergeFragmentHashTag( @Param("name") String tagName );
	
	int mergeFragmentHashTagRecordByName( @Param("name")String name, @Param("fragmentNo") long fragmentNo );
}
