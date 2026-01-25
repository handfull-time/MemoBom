package com.utime.memoBom.board.vo;

public enum EEmotionTargetType {
	Board(1),
	Comment(2);
	
	final int code;
	
	private EEmotionTargetType(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static EEmotionTargetType of(int code) {
		for(EEmotionTargetType type : EEmotionTargetType.values()) {
			if(type.getCode() == code) {
				return type;
			}
		}
		return null;
	}
}
