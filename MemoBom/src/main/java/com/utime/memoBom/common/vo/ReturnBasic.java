package com.utime.memoBom.common.vo;

public class ReturnBasic {

	protected String code;
	protected String message;
	
	protected Object data;
	
	public ReturnBasic() {
		this(AppDefine.ERROR_OK, null);
	}
	
	public ReturnBasic(String code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public ReturnBasic setCodeMessage(String code, String message) {
		this.code = code;
		this.message = message;
		
		return this;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isError() {
		return ! AppDefine.ERROR_OK.equals(this.code);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ReturnBasic [code=" + code + ", message=" + message + ", data=" + data + "]";
	}
}
