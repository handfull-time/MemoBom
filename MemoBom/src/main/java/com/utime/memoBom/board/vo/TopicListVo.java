package com.utime.memoBom.board.vo;

import java.util.List;

import lombok.Data;

@Data
public class TopicListVo {
	/** 인기 토픽 */
	List<TopicVo> trending;
	/** 최신 토픽 */
	List<TopicVo> fresh;
}
