package com.utime.memoBom.board.dto;

import com.utime.memoBom.board.vo.EmojiSetType;

import lombok.Data;

@Data
public class TopicSaveDto {
	/** 검증 키 */
	String seal;
	/** 고유 아이디 */
	String uid;
	/** 이름 */
	String name;
	/** 설명 */
	String description;
	/** 색상 */
	String color;
	/** 공개 여부 */
	boolean external;
	/** 이모지 */
	String imogi;
	/** 이모지 세트 타입 */
	EmojiSetType emojiSetType;
	/** 감정 분석 사용 여부 */
	boolean emotion;
	/** 댓글 기능 사용 여부 */
	boolean comments;
	/** AI 기능 사용 여부 */
	boolean ai;
	/** AI 프롬프트 */
	String prompt;
	/** 최대 길이 */
    int maxLen;
}
