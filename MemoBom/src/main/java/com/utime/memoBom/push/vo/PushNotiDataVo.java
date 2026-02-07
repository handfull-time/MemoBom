package com.utime.memoBom.push.vo;

import lombok.Data;

/**
 * 푸시 전송 정보
 */
@Data
public class PushNotiDataVo {
	String title;
	String message;
	/**
	 * 아이콘 경로. full url 주소던가 아니면 contextPath를 포함
	 */
	String icon;

	@Data
	public static class _Data{
		String contextPath;
		/**
		 * 추적용 click id
		 */
		String clickId;
		/**
		 * full url 주소던가 아니면 contextPath를 미포함
		 */
		String url;
	}
	
	_Data data = new _Data();
}
