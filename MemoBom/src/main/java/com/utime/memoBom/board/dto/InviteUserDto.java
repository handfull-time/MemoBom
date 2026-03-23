package com.utime.memoBom.board.dto;

import lombok.Data;
/**
 * 사용자 초대 정보
 */
@Data
public class InviteUserDto {
	/** 초대할 topic UID */
	String topicUid;
	/** 초대할 사람 UID */
	String userUid;
}
