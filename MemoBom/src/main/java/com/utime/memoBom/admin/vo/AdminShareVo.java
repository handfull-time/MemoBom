package com.utime.memoBom.admin.vo;

import lombok.Data;

@Data
public class AdminShareVo {
	String createAt;
	Long shareNo;
	String uid;
	Short targetType;
	Long targetNo;
	Long userNo;
	String userId;
	String userNickname;
	String topicName;
	String fragmentContent;
}
