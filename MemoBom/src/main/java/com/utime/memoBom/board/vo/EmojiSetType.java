package com.utime.memoBom.board.vo;

public enum EmojiSetType {
    EMOTION(1), 
    REACTION(2);
    
	public final int code;
    
    EmojiSetType(int code){ 
    	this.code = code; 
    }
    
    public static EmojiSetType of(int code) {
		for(EmojiSetType type : values()) {
			if(type.code == code) {
				return type;
			}
		}
		return null;
	}
}
