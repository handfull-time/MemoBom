package com.utime.memoBom.user.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MyCommentDto {
	/** 댓글 고유 아이디 */
	String uid;
	/** 내용 */
	String content;
	/** 생성일 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	Date regDate;
	
	MyFragmentDto fragment = new MyFragmentDto();
}
