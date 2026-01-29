package com.utime.memoBom.board.dto;

import lombok.Data;

/**
 * Board 메인 검색 조건
 * Fragments.json 사용
 */
@Data
public class BoardMainParamDto {
	/** 사용자 uid */
	String userUid;
	/** Fragment uid */
	String fragUid;
	/** topic uid */
	String topicUid;
	/** 댓글 uid */
	String cmtUid;
	/** 검색어 */
	String keyword;
	/** 해쉬테그 */
	String hashtag;
}
