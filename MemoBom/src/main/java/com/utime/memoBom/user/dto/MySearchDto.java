package com.utime.memoBom.user.dto;

import lombok.Data;

/**
 * My page 검색 정보
 */
@Data
public class MySearchDto {
	/** 검색어 */
	String keyword;
	/** 페이지 번호 */
	int pageNo = 1;
}
