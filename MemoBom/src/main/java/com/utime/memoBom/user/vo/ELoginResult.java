package com.utime.memoBom.user.vo;

/**
 * 로그인 결과
 */
public enum ELoginResult {
	/** 성공 */
	Success
	/** id 없음 */
	, IdNotFound
	/** 권한 X */
	, Denied
	/** 비번 틀림 */
	, MismatchPw
	/** 가입 */
	, Join;
}
