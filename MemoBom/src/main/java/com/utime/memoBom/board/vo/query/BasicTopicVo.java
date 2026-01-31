package com.utime.memoBom.board.vo.query;

import com.utime.memoBom.board.vo.EmojiSetType;

import lombok.Data;

/**
 * 토픽 기본 정보
 */
@Data
public class BasicTopicVo {
	/** 고유 id */
	String uid;
	/** 이름 */
	String name;
	/** 색 #RRGGBB 또는 'indigo' */
	String color;
	/** 이모지 */
	String imogi;
	/** 이모지 세트 타입*/
	EmojiSetType emojiSetType = EmojiSetType.EMOTION;
}
