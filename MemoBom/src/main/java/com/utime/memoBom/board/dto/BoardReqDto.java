package com.utime.memoBom.board.dto;

import lombok.Data;

/**
 * fragment 작성 정보
 */
@Data
public class BoardReqDto {
	/** fragment 고유 키 */
	String uid;
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
	/** 삭제 여부. true:삭제. false:수정 */
	boolean deleted = false;
}
