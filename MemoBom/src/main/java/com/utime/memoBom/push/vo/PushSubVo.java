package com.utime.memoBom.push.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PushSubVo {

    private long subNo;

    private long userNo;

    /** push endpoint (unique) */
    private String endPoint;

    private String p256dh;
    private String auth;

    /** 활성 여부 */
    private boolean active;

    /** 기기 구분 */
    private String deviceId;

    private String userAgent;
    private String browser;
    private String os;

    private LocalDateTime lastPushDate;

    private int failCount;

    private long expirationTime;
}

