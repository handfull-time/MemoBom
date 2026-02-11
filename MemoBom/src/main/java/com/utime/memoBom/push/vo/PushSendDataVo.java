package com.utime.memoBom.push.vo;

import lombok.Data;

/**
 * 푸시 전송 데이터
 */
@Data
public class PushSendDataVo {
	/** 제목 */
	String title;
	/** 내용 */
	String message;
	/** 링크 URL */
	String linkUrl;
	/** 이미지 URL */
	String imageUrl;
	
}
