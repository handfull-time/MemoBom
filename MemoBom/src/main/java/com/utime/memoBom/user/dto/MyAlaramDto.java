package com.utime.memoBom.user.dto;

import java.util.Date;

import com.utime.memoBom.user.vo.EAlaramType;

import lombok.Data;

@Data
public class MyAlaramDto {
	Date createDate;
	Date readDate;
	EAlaramType alarmType;
	String title;
	String message;
	String url;
}
