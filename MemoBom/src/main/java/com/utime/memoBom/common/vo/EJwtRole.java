package com.utime.memoBom.common.vo;

/**
 * Enum  이름에 따라 템플릿 폴더가 생성돼야 한다.<br/>
 * App 권한이면 template/App/ 이하 경로에 템플릿 파일이 있어야 함.
 * @author utime
 *
 */
public enum EJwtRole {
    Admin("Administrator"),
    User("User");

    private final String dscr;
    
    private EJwtRole(String s) {
		this.dscr = s;
	}
    
    public String getDscr() {
		return dscr;
	}
    
}