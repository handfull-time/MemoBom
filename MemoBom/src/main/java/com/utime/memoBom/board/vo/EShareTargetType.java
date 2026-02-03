package com.utime.memoBom.board.vo;

public enum EShareTargetType {
	Topic(0),
	Board(1),
	Comment(2);
	
	final int code;
	
	private EShareTargetType(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static EShareTargetType of(int code) {
		for(EShareTargetType type : EShareTargetType.values()) {
			if(type.getCode() == code) {
				return type;
			}
		}
		return null;
	}
}
