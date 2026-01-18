package com.utime.memoBom.board.mapper;

import org.apache.ibatis.annotations.Mapper;

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
	
	
}
