package com.utime.memoBom.board.vo;

import lombok.Data;

@Data
public class TopicReqVo {
	/** 고유번호 */
	long topicNo = -1L;
	/** 소유자 번호 */
	long ownerNo;
	/** 사용 여부 */
	boolean enabled = true;
	/** 이름 */
	String name;
	/** 검색 해쉬 값 ex) #내용 #내용 ... */
	String hashTag;
	/** 설명 */
	String description;
	/** 색 #RRGGBB 또는 'indigo' */
	String color;
	/** 외부 공개 여부 true:공개, false:비공개 */
	boolean external = true;
	/** 이모지 */
	String imogi;
	/** 이모지 세트 타입*/
	EmojiSetType emojiSetType = EmojiSetType.EMOTION;
}
