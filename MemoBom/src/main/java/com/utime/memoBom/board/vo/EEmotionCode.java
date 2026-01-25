package com.utime.memoBom.board.vo;

public enum EEmotionCode {
    JOY("joy", "😊"),
    NORMAL("normal", "😐"),
    SADNESS("sadness", "😢"),
    ANGER("anger", "😠"),
    AWESOME("awesome", "🤩"),

    LIKE("like", "👍"),
    EMPATHY("empathy", "❤️"),
    FUNNY("funny", "😂"),
    SURPRISE("surprise", "😮");

    public final String code;
    public final String emoji;

    EEmotionCode(String code, String emoji) {
        this.code = code;
        this.emoji = emoji;
    }
    
    public String getCode() {
		return code;
	}
    
    public String getEmoji() {
		return emoji;
	}
    
    public static EEmotionCode fromCode(String code) {
		for (EEmotionCode emotion : EEmotionCode.values()) {
			if (emotion.code.equals(code)) {
				return emotion;
			}
		}
		throw new IllegalArgumentException("Unknown code: " + code);
	}
}
