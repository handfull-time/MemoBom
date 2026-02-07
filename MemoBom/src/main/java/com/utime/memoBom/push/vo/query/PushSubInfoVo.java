package com.utime.memoBom.push.vo.query;

import lombok.Data;

@Data
public class PushSubInfoVo {
	private long subNo;

    /** push endpoint (unique) */
    private String endPoint;

    private String p256dh;
    private String auth;
    
    private int failCount;
}
