package com.utime.memoBom.board.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.utime.memoBom.board.vo.BoardReqVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.user.vo.UserVo;

/**
 * 게시글 처리
 */
@Mapper
public interface BoardMapper {

	int createMemoBoard();
	
	
	int createMemoComments();
	
	int createMemoScrap();
	
	
	int createMemoEmotionLog();
	
	int upsertEmotion(long userNo, int targetType, long targetNo, String emotion);
	
	int deleteEmotion(long userNo, int targetType, long targetNo);
	
	/**
	 * Fragment(편린) 저장
	 * @param reqVo
	 * @return
	 */
	int saveFragment(UserVo user, TopicVo topic, BoardReqVo reqVo);
}
