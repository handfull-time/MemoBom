package com.utime.memoBom.user.vo;


import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 요청 정보
 */
@Setter
@Getter
public class LoginReqVo extends ReqUniqueVo{
	
	/**
	 * id 
	 */
	private String id;

	/**
	 * 암호
	 */
	private String pw;
	
	/**
	 * 접속 브라우져 정보
	 */
	private String userAgent;
	
}
