package com.utime.memoBom.board.vo;

import com.utime.memoBom.user.vo.UserVo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class TopicVo extends TopicReqVo{
	UserVo user;
	boolean flow;
	int fragmentCount;
	int flowCount;
}
