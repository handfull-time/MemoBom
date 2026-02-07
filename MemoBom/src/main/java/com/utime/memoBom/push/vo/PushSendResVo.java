package com.utime.memoBom.push.vo;

import com.utime.memoBom.push.vo.query.PushSubInfoVo;

/**
 * 푸시 발송 결과
 */
public record PushSendResVo (PushSubInfoVo sub, Boolean status ) {};
