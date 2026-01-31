package com.utime.memoBom.board.vo.query;

import java.util.Date;

import lombok.Data;

@Data
public class MyCommentVo {
	/** 댓글 고유 아이디 */
	String uid;
	/** 내용 */
	String content;
	/** 생성일 */
	Date regDate;

	String fragmentUid;
	
	String fragmentPreview;
	
	BasicTopicVo topic;
}
