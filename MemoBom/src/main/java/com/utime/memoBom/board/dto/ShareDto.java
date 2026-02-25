package com.utime.memoBom.board.dto;

import lombok.Data;

/**
 * 공유하기 DTO
 */
@Data
public class ShareDto {
	/** 제목 */
	String title;
	/** 내용 */
	String text;
	/** URL */
	String url;
	/** URL */
	String image;
}
