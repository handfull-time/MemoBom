package com.utime.memoBom.admin.vo;

import lombok.Data;

@Data
public class AdminPushVo {
	String regDate;
	Long subNo;
	Long userNo;
	String userId;
	String userNickname;
	Boolean active;
	String deviceId;
	String browser;
	String os;
	Long failCount;
	String lastPushDate;
}
