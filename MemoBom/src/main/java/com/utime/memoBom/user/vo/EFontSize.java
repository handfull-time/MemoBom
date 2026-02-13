package com.utime.memoBom.user.vo;

/**
 * 글자 크기
 */
public enum EFontSize {
	xs, sm, md, lg, xl;
	
	public static EFontSize of( String val ) {
		if( val == null )
			return null;
		
		val = val.toLowerCase();
		
		for(EFontSize type : EFontSize.values()) {
			if(type.name().equals(val) ) {
				return type;
			}
		}
		return null;
	}
}
