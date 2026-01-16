package com.utime.memoBom.common.vo;

import lombok.Data;

@Data
public class UserDevice {
	/** 장치 종류 */
	private EDevicePlatform device;
	/** 모델 명 */
	private String model;
	
	public UserDevice( EDevicePlatform d, String m) {
		this.device = d;
		this.model = m;
	}
}
