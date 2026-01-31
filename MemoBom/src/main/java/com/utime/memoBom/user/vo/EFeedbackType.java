package com.utime.memoBom.user.vo;

/**
 * 피드백 종류
 */
public enum EFeedbackType {
	/** 오류 보고 */
	BUG("BG", "Bug Report"),
	/** 기능 제안 */
    FEATURE("FR", "Feature Request"),
	/** 성능/사용성 개선  */
    ENHANCEMENT("IM", "Improvement"),
	/** 기타 문의 */
    OTHERS("GI", "General Inquiry");

    private final String code;
    private final String description;

    EFeedbackType(String code, String description) {
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
    public static EFeedbackType fromCode(String code) {
        for (EFeedbackType type : EFeedbackType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown FeedbackType code: " + code);
    }
}
