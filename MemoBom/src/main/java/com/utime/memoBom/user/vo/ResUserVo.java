package com.utime.memoBom.user.vo;

import com.utime.memoBom.common.vo.ReturnBasic;

public class ResUserVo extends ReturnBasic {

	private UserVo user;
	
	public ResUserVo(String c, String m) {
		super(c, m);
	}
	
	public ResUserVo() {
		super();
	}

	public UserVo getUser() {
		return user;
	}

	public void setUser(UserVo user) {
		this.user = user;
	}

}
