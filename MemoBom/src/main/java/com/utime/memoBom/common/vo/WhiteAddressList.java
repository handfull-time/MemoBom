package com.utime.memoBom.common.vo;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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
			"/manifest.webmanifest"
			, "/sw.js"
			, "/js/"
			, "/images/"
			, "/css/"
			, "/Auth/"
			, "/oauth2/"
			, "/login/"
			, "/Error/"
			, "/error/"
			, "/DbConsoleH2/"
			, "/Test/"
		};
	
}
