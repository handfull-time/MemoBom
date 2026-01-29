package com.utime.memoBom.board.vo;

import com.utime.memoBom.board.dto.BoardMainParamDto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Fragment 검색 조건
 */
@Setter
@Getter
@ToString(callSuper = true)
public class FragmentListReqVO extends BoardMainParamDto {
	/** 페이징. 1부터 시작 */
	int pageNo = 1;
}
