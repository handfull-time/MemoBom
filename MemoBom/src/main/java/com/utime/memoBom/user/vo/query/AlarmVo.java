package com.utime.memoBom.user.vo.query;

import java.util.Date;

import com.utime.memoBom.user.vo.EAlaramType;

import lombok.Data;

@Data
public class AlarmVo {
	String uid;
	Date createDate;
	Date readDate;
	EAlaramType alarmType;
	String title;
	String message;
	String url;
}
