package com.utime.memoBom.user.vo;

/**
 * 알람 종류
 */
public enum EAlaramType {
	Info("I", "정보")
	, Event("E", "이벤트");
	
	private final String code;
    private final String description;

    EAlaramType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    // 데이터베이스 코드 값으로 Enum을 찾는 역방향 조회 메서드
    public static EAlaramType fromCode(String code) {
        for (EAlaramType type : EAlaramType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown EAlaramType code: " + code);
    }
}
