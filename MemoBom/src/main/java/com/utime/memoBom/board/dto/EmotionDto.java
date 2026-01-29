package com.utime.memoBom.board.dto;

import com.utime.memoBom.board.vo.EEmotionCode;
import com.utime.memoBom.board.vo.EEmotionTargetType;
import com.utime.memoBom.board.vo.EmojiSetType;

import lombok.Data;

@Data
public class EmotionDto {
	/**  uid */
	String uid;
	/** 감정 상태 */
	EEmotionCode emotion;
	/** 이모지 세트 타입*/
	EmojiSetType emojiSetType;
	/** 감정 대상 타입 */
	EEmotionTargetType targetType;
}