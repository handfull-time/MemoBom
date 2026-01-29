package com.utime.memoBom.board.dto;

import com.utime.memoBom.board.vo.EmojiSetType;

import lombok.Data;

/**
 * 토픽(주제) 정보 DTO
 */
@Data
public class TopicDto {
	/** 고유 id */
	String uid;
	/** 이름 */
	String name;
	/** 설명 */
	String description;
	/** 색 #RRGGBB 또는 'indigo' */
	String color;
	/** 외부 공개 여부 true:공개, false:비공개 */
	boolean external = true;
	/** 이모션 사용 여부 true:사용, false:미사용 */
	boolean emotion = true;
	/** 댓글 사용 여부 true:사용, false:미사용 */
	boolean comments = true;
	/** 최대 크기. 0 무제한 */
	int maxLen = 500;
	/** 이모지 */
	String imogi;
	/** 이모지 세트 타입*/
	EmojiSetType emojiSetType = EmojiSetType.EMOTION;
}
