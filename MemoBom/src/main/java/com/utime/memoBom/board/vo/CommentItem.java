package com.utime.memoBom.board.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.utime.memoBom.user.vo.UserVo;

import lombok.Data;

/**
 * 댓글 내용
 */
@Data
public class CommentItem {

	/** 댓글 고유 아이디 */
	String uid;
	/** 작성자 */
	UserVo user;
	/** 내용 */
	String content;
	/** 생성일 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	Date regDate;
	/** 이모션 목록 */
	List<EmotionItem> emotionList;
}
