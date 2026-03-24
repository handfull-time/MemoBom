package com.utime.memoBom.board.dto;

import lombok.Data;

/**
 * 초대 수락/거절 요청 정보
 */
@Data
public class InviteDecisionDto {
	/** 대상 topic UID */
	String topicUid;
	/** true: 수락, false: 거절 */
	boolean accept;
}
