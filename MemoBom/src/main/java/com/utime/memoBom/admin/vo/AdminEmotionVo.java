package com.utime.memoBom.admin.vo;

import lombok.Data;

@Data
public class AdminEmotionVo {
	String regDate;
	String updateDate;
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
