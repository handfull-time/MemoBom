package com.utime.memoBom.board.vo;

import lombok.Data;

/**
 * 공유하기 VO
 */
@Data
public class ShareVo {
	/** 제목 */
	String title;
	/** 내용 */
	String text;
	/** URL */
	String url;
}
