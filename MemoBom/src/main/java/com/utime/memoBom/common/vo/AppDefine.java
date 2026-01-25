package com.utime.memoBom.common.vo;

public class AppDefine {
	public static final String ERROR_OK = "0";
	
	public static final String KeyBeforeUri = "BeforeUri";

	public static String KeyParamUser = "user";
	
	/** 리프래쉬 토큰의 쿠키 이름 */
	public static final String KeyRefreshToken = "refreshToken";
	
	/** YES, True */
	public static final String YES = "Y";
	/** NO, False */
	public static final String NO = "N";
	
	public static final boolean IsLinux = System.getProperty("os.name").toLowerCase().indexOf("windows") < 0;
	
	public static final String KeySeedKey = "env.seed.key";
	
	public static final String KeySeedIV = "env.seed.iv";
	/** 탈퇴 처리 위한 쿠키 키 */
	public static final String WithdrawMode = "WITHDRAW_MODE";
	
	public static final String KeyJwtSecret = "jwt.secret";
	
	/** PWA 푸시 private 키 */
	public static final String KeyPushPrivate = "env.push.private";
	/** PWA 푸시 public 키 */
	public static final String KeyPushPublic = "env.push.public";
	
	public static final String KeyShowHeader = "showHeader";
	
	public static final String KeyShowFooter = "showFooter";
	
	public static final String KeyLoadScript = "loadScript";
	
	public static final String AssetVersion = String.valueOf(System.currentTimeMillis());
	
}
