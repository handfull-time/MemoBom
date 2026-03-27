package com.utime.memoBom.admin.vo;

import lombok.Data;

@Data
public class AdminSearchVo {
	/** 날짜 */
	String date;
	/** 페이지 번호 */
	int pageNo = 1;
	/** 검색 키워드 */
	String keyword;
}
