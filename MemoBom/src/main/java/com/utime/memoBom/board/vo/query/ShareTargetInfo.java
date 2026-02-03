package com.utime.memoBom.board.vo.query;

import lombok.Data;

/**
 * 공유 대상 정보
 */
@Data
public class ShareTargetInfo {
	/** 대상 고유 번호 */
	long targetNo;
	/** 내용 */
	String text;
}
