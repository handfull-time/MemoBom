package com.utime.memoBom.common.vo;

import java.security.Principal;

/**
 * 웹소켓 통신시 고유 값 
 */
public class StompPrincipal implements Principal{

	protected final String name;
	
	public StompPrincipal(final String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return "StompPrincipal:" + this.name;
	}
}
