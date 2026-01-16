package com.utime.memoBom.common.vo;

import java.util.Date;

import lombok.Data;

/**
 * DB Binary 정보 조회
 */
@Data
public class BinResultVo{
	private byte [] binary;
	private Date lastDate;
}

