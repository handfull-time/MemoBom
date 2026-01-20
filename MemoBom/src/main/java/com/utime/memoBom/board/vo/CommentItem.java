package com.utime.memoBom.board.vo;

import java.util.Date;
import java.util.List;

import com.utime.memoBom.user.vo.UserVo;

/**
 * 댓글 내용
 */
public class CommentItem {

	/** 고유 번호 */
	long no;
	/** 작성자 */
	UserVo user;
	/** 내용 */
	String comments;
	/** 생성일 */
	Date regDate;
	/** 이모션 목록 */
	List<EmotionItem> emotionList;
}
