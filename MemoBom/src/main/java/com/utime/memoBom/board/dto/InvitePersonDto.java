package com.utime.memoBom.board.dto;

import lombok.Data;

@Data
public class InvitePersonDto {
	/** 초대할 topic UID */
	String topicUid;
	/** 초대할 사람 UID */
	String userUid;
}
