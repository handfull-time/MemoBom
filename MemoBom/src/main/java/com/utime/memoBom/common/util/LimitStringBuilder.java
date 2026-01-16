package com.utime.memoBom.common.util;

/**
 * 용량 제한 StringBuffer
 */
public class LimitStringBuilder {

	private final StringBuilder sb;
	
	private final int maxLength;
	
	/**
	 * 최대 용량
	 * @param maxLength
	 */
	public LimitStringBuilder(int maxLength) {
		this.sb = new StringBuilder(maxLength + 8);
		this.maxLength = maxLength;
	}
	
	/**
	 * str 추가.
	 * @param str
	 * @return
	 */
	public LimitStringBuilder append(String str) {
		final int length = this.sb.length();
		
		if( this.maxLength <= length )
			return this;
		
		if( this.maxLength < (length + str.length()) ) {
			str = str.substring(0, this.maxLength - length);
		}
		
		this.sb.append(str);
        return this;
    }
	
	public LimitStringBuilder append(LimitStringBuilder sb) {
        return this.append(sb.toString());
    }
	
	public LimitStringBuilder append(Object obj) {
		if( obj == null )
			return this;
		
        return this.append(obj.toString());
    }
	
    public LimitStringBuilder append(char[] str) {
        return this.append(str, 0, str.length);
    }

    public LimitStringBuilder append(char[] str, int offset, int len) {
		if( str == null || len <= 0 || offset < 0)
			return this;
		
    	final int length = this.sb.length();
		
		if( this.maxLength <= length )
			return this;
		
		if( this.maxLength < (length + len) ) {
			len = this.maxLength - length;
		}
		
    	this.sb.append(str, offset, len);
        return this;
    }

    public LimitStringBuilder append(boolean b) {
        return this.append(Boolean.toString(b));
    }

    public LimitStringBuilder append(int i) {
        return this.append(Integer.toString(i));
    }

    public LimitStringBuilder append(long lng) {
        return this.append(Long.toString(lng));
    }

    public LimitStringBuilder append(float f) {
        return this.append(Float.toString(f));
    }

    public LimitStringBuilder append(double d) {
    	return this.append(Double.toString(d));
    }
    
    /**
     * Returns the length (character count).
     * @return
     */
    public int length() {
    	return this.sb.length();
    }

    public LimitStringBuilder clear() {
    	this.sb.setLength(0);
    	return this;
    }

    @Override
    public String toString() {
    	return this.sb.toString();
    }

}
