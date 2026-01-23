package com.utime.memoBom.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.board.vo.FragmentItem;
import com.utime.memoBom.board.vo.FragmentListReqVO;
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
	
	/**
     * 게시글 목록 조회
     * @param user 현재 사용자 정보
     * @param reqVo 검색 조건
     * @return FragmentItem 리스트
     */
    List<FragmentItem> loadFragmentList(
    	@Param("user") UserVo user,
        @Param("req") FragmentListReqVO reqVo
    );

}
