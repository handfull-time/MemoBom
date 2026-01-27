package com.utime.memoBom.user.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.utime.memoBom.common.vo.EJwtRole;

import lombok.Data;

/**
 * 사용자 정보
 */
@Data
public class UserVo {
	/** 회원 번호 */
	long userNo;
	/** 생성일 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-DD HH:mm", timezone = "Asia/Seoul")
	LocalDateTime regDate;
	/** 수정일 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-DD HH:mm", timezone = "Asia/Seoul")
	LocalDateTime updateDate;
	/** 사용 여부 */
	boolean enabled;
	/** 권한 */
	EJwtRole role;
	/** 가입 경로 */
	String provider;
	/** 사용자 고유 ID */
	String uid;
	/** id */
	String id;
	/** 사용자 email */
	String email;
	/** 닉네임 */
	String nickname;
	/** 비고 */
	String note;
	/** 이미지 URL */
	String profileUrl;
}
