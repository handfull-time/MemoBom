package com.utime.memoBom.user.dto;

import com.utime.memoBom.user.vo.EFontSize;

/**
 * 사용자 정보 DTO
 */
public record UserDto (
	/** 고유 아이디 */
	String uid,
	/** 별명 */
	String nickname,
	/** 프로필 이미지 URL */
	String profileUrl,
	/** FONT 크기 */
	EFontSize fontSize
) {};
