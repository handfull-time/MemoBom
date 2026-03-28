package com.utime.memoBom.admin.vo;

import lombok.Data;

@Data
public class AdminCommentVo {
	String regDate;
	String updateDate;
	Long commentNo;
	String uid;
	Boolean deleted;
	Long fragmentNo;
	String fragmentUid;
	String fragmentContent;
	Long topicNo;
	String topicName;
	Long userNo;
	String userId;
	String userNickname;
	String content;
}
