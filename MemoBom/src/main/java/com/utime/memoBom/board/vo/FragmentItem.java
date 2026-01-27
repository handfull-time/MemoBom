package com.utime.memoBom.board.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
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
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	Date regDate;
	/** 내용 */
	String content;
	/** 스크랩 여부 */
	boolean scrap;
	/** 이모션 목록 */
	List<EmotionItem> emotionList;
	/** 해시 태그 목록 */
	List<String> hashtagList;
}
