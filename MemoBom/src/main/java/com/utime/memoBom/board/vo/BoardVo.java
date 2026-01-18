package com.utime.memoBom.board.vo;

import java.util.Date;

import com.utime.memoBom.user.vo.UserVo;

import lombok.Data;

@Data
public class BoardVo {
	/** 고유 id */
	String uid;
	
	/** 작성일 */
	Date regDate;
	
	/** 작성자 */
	UserVo user;
	
	/** 주제 */
	TopicVo topic;
	
	/** 메모 */
	String memo;
	
	/** 스크랩 여부 */
	boolean scraped;
	
	
}
