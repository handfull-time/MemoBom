package com.utime.memoBom.admin.vo;

import lombok.Data;

/**
 * 전체 통계
 */
@Data
public class DashboardVo {
	int user;
	int topic;
	int fragment;
	int comment;
	int emotion;
	int scrap;
	int share;
	int push;
}
