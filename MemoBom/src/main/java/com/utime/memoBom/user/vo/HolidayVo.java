package com.utime.memoBom.user.vo;

import lombok.Data;

/**
 * 공휴일 정보
 */
@Data
public class HolidayVo {
	/**
	 * 특일정보 분류(01:국경일/공휴일, 02:기념일, 03:24절기, 04:잡절)
	 */
	int dateKind;
	/** 명칭 */
    String dateName;
    /** 공공기관 휴일 여부 */
    boolean holiday;
    /** 날짜 */
    String locdate;
}
