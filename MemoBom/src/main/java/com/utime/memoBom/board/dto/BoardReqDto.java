package com.utime.memoBom.board.dto;

import lombok.Data;

/**
 * fragment 작성 정보
 */
@Data
public class BoardReqDto {
	/** 검증 키 */
	String seal;
	/** topic uid */
	String topicUid;
	/** 내용 */
	String content;
	/** 해쉬태그 */
	String hashTag;
	/** 작성 ip */
	String ip;
}
