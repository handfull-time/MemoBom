package com.utime.memoBom.admin.vo;

import lombok.Data;

@Data
public class AdminFragmentVo {
	String regDate;
	String updateDate;
	Long fragmentNo;
	String uid;
	Boolean deleted;
	Long userNo;
	String userId;
	String userNickname;
	Long topicNo;
	String topicName;
	String topicUid;
	String content;
	Long commentCount;
	Long emotionCount;
	Long scrapCount;
}
