package com.utime.memoBom.admin.vo;

import lombok.Data;

@Data
public class AdminTopicVo {
	String regDate;
	String updateDate;
	Long topicNo;
	Long ownerNo;
	String ownerNickname;
	Boolean enabled;
	String uid;
	Boolean external;
	Boolean emotion;
	Boolean comments;
	Integer maxLen;
	String name;
	String nameUpper;
	String description;
	String imogi;
	String color;
	Integer emojiSetType;
	Boolean ai;
	String prompt;
}
