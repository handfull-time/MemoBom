package com.utime.memoBom.board.vo;

import com.utime.memoBom.user.vo.UserVo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class TopicVo extends TopicReqVo{
	/** 회원 */
	UserVo user;
	/** 팔로우 여부 */
	boolean flow;
	/** 게시글 수 */
	int fragmentCount;
	/** 팔로우 수 */
	int flowCount;
}
