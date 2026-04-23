package com.utime.memoBom.admin.vo;

import lombok.Data;

@Data
public class AdminUserVo {
	String createdAt;
	String updatedAt;
	Long userNo;
	Boolean enabled;
	String role;
	String provider;
	String id;
	String email;
	String nickname;
	String note;
	String profileUrl;
}
