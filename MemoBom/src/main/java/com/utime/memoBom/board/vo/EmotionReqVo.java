package com.utime.memoBom.board.vo;

import lombok.Data;

@Data
public class EmotionReqVo {
	/** fragment uid */
	String uid;
	/** 감정 상태 */
	EEmotionCode emotion;
	/** 이모지 세트 타입*/
	EmojiSetType emojiSetType;
	/** 감정 대상 타입 */
	EEmotionTargetType targetType;
}