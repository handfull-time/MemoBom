package com.utime.memoBom.board.vo;

import lombok.Data;

@Data
public class BoardReqVo {
	String seal;
	String topicUid;
	String content;
	String hashTag;
	String ip;
}
