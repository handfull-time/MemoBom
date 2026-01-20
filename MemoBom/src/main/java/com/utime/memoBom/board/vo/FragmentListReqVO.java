package com.utime.memoBom.board.vo;

import lombok.Data;

/**
 * Fragment 검색 조건
 */
@Data
public class FragmentListReqVO {
	/** 사용자 uid */
	String userUid;
	/** topic uid */
	String topicUid;
	/** 검색어 */
	String keyword;
	/** 해쉬테그 */
	String hashTag;
	/** 페이징. 1부터 시작 */
	int pageNo = 1;
}
