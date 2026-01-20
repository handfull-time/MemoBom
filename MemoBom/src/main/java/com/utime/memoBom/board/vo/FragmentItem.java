package com.utime.memoBom.board.vo;

import java.util.Date;
import java.util.List;

import com.utime.memoBom.user.vo.UserVo;

import lombok.Data;

@Data
public class FragmentItem {
	/** 게시글 uid */
	String uid;
	/** topic 정보 */
	TopicVo topic;
	/** 사용자 정보 */
	UserVo user;
	/** 생성일 */
	Date regDate;
	/** 내용 */
	String content;
	/** 스크랩 여부 */
	boolean scrap;
	/** 이모션 목록 */
	List<EmotionItem> emotionList;
}
