package com.utime.memoBom.board.vo.query;

import com.utime.memoBom.board.vo.EShareTargetType;

import lombok.Data;

/**
 * 공유 데이터 타입
 */
@Data
public class ShareDataVo {
	/** 고유 번호 */
	long shareNo;
	/** 고유 키 */
	String uid;
	/** 사용자 구분 값 */
	long userNo;
	/** 타겟 종류 */
	EShareTargetType targetType;
	/** 타겟 번호 */
	long targetNo;
}
