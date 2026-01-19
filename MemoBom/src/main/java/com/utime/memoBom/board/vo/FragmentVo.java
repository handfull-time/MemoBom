package com.utime.memoBom.board.vo;

import java.util.Date;

import lombok.Data;

@Data
public class FragmentVo {
	long fragmentNo;
	long userNo;
	long topicNo;
	String content;
	String uid;
	String ip;
	String device;
	Date regDate;
}
