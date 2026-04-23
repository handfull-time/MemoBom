package com.utime.memoBom.admin.vo;

import lombok.Data;

@Data
public class AdminEmotionVo {
	String createdAt;
	String updatedAt;
	Long emotionLogId;
	Short targetType;
	Long targetNo;
	Long userNo;
	String userId;
	String userNickname;
	String emotion;
	Long fragmentNo;
	String fragmentContent;
	Long commentNo;
	String commentContent;
	String topicName;
}
