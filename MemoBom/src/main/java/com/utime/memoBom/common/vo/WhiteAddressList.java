package com.utime.memoBom.common.vo;

/**
 * white list (Spring Security 체크 제외 목록)
 * @author utime
 *
 */
public class WhiteAddressList {

	/**
	 * white list
	 */
	public static String [] AddressList = new String[] {
			"/Auth/"
			, "/Share/"
			, "/oauth2/"
			, "/login/"
//			, "/Error/"
			, "/error/"
			, "/DbConsoleH2/"
			, "/Test/"
		};
}
