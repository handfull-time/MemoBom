package com.utime.memoBom.board.vo;

import lombok.Data;

@Data
public class TopicReqVo {
	/** 검증 키 */
	String seal;
	/** 고유번호 */
	long topicNo = -1L;
	/** 고유 id */
	String uid;
	/** 소유자 번호 */
	long ownerNo;
	/** 사용 여부 */
	boolean enabled = true;
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
	int maxLen = 0;
	/** 이모지 */
	String imogi;
	/** 이모지 세트 타입*/
	EmojiSetType emojiSetType = EmojiSetType.EMOTION;
}
