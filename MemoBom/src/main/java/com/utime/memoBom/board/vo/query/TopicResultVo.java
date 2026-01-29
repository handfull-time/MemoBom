package com.utime.memoBom.board.vo.query;

import com.utime.memoBom.board.vo.EmojiSetType;
import com.utime.memoBom.user.vo.query.BasicUserVo;

import lombok.Data;

@Data
public class TopicResultVo {
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
	/** 팔로우 여부 */
	boolean flow;
	/** 팔로우 수 */
	int flowCount;
	/** 게시글 수 */
	int fragmentCount;
	/** 소유자 정보 */
	BasicUserVo user;
}
