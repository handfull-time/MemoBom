package com.utime.memoBom.user.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MyFragmentDto {
	/** 생성일 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	Date regDate;
	/** 게시글 uid */
	String uid;
	/** 내용 */
	String content;
	/** topic 정보 */
	MyTopicDto topic = new MyTopicDto();
}
