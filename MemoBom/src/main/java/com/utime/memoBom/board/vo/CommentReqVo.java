package com.utime.memoBom.board.vo;

import lombok.Data;

/**
 * 댓글 저장 요청 VO
 */
@Data
public class CommentReqVo {
	/** 댓글 고유 번호 (insert에서 사용) */
	long commentNo;
	/** 편린 고유 아이디(req 필수) */
	String uid;
	/** 내용 (req 필수) */
	String content;
	/** 기기 정보 */
	String device;
	/** IP 주소 */
	String ip;
	/** 이모지 세트 타입 (req 필수)*/
	EmojiSetType emojiSetType;
}
