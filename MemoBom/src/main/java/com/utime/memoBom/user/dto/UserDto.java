package com.utime.memoBom.user.dto;

import lombok.Data;

/**
 * 사용자 정보 DTO
 */
@Data
public class UserDto {
	/** 고유 아이디 */
	String uid;
	/** 별명 */
	String nickname;
	/** 프로필 이미지 URL */
	String profileUrl;
}
