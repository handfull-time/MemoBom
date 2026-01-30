package com.utime.memoBom.user.dto;

import lombok.Data;

/**
 * My page 검색 정보
 */
@Data
public class MySearchDto {
	/** 검색어 */
	String keyword;
	/**  */
	int pageNo = 1;
}
