package com.utime.memoBom.user.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * 사용자 정보 수정용 DTO
 */
@Data
public class UserUpdateDto {
	/** 고유 아이디 */
	String uid;
	/** 별명 */
	String nickname;
	/** 프로필 이미지  */
	MultipartFile profile;
}
