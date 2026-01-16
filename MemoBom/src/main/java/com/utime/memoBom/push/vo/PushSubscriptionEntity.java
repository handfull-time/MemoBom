package com.utime.memoBom.push.vo;

import lombok.Data;

@Data
public class PushSubscriptionEntity {

	private long subNo = -1L;
	
    private long userNo;

    private String endPoint;
    
    private String p256dh;

    private String auth;
}
