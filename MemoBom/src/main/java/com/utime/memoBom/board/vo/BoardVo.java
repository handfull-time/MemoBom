package com.utime.memoBom.board.vo;

import java.util.Date;

import com.utime.memoBom.user.vo.UserVo;

import lombok.Data;

@Data
public class BoardVo {
	/** 고유 id */
	String uid;
	
	/** 작성일 */
	Date createdAt;
	
	/** 작성자 */
	UserVo user;
	
	/** 얘깃거리 */
	TopicVo topic;
	
	/** 메모 */
	String memo;
	
	/** 보관함 여부 */
	boolean scraped;
	
	
}
