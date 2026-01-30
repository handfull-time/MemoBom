package com.utime.memoBom.user.dto;

import lombok.Data;

@Data
public class MyPageDto {
	/** 사용자 정보 */
	UserDto user;
	
	/** 이용 통계 */
	UsageStatisticsDto statistics;
}
