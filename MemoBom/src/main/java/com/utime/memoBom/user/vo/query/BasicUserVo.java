package com.utime.memoBom.user.vo.query;

import lombok.Data;

/**
 * 기본 사용자 정보 VO
 */
@Data
public class BasicUserVo {
	/** 사용자 고유 ID */
	String uid;
	/** 닉네임 */
	String nickname;
	/** 이미지 URL */
	String profileUrl;
}
