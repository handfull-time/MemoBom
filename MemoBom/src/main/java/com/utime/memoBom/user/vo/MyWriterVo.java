package com.utime.memoBom.user.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 글 작성 정보
 */
@Data
public class MyWriterVo {
	
	/** 작성 일자 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	Date date;
	/** 작성 종류 */
	ETarget target;
	/** uid */
	String uid;
	/** 이름 공휴일/기념일 사용 */
	String name;
}
