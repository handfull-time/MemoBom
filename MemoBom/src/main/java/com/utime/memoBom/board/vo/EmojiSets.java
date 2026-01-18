package com.utime.memoBom.board.vo;

import java.util.List;

public final class EmojiSets {
    public static final List<EEmotionCode> EMOTION = List.of(
        EEmotionCode.JOY,
        EEmotionCode.NORMAL,
        EEmotionCode.SADNESS,
        EEmotionCode.ANGER,
        EEmotionCode.AWESOME
    );

    public static final List<EEmotionCode> REACTION = List.of(
        EEmotionCode.LIKE,
        EEmotionCode.EMPATHY,
        EEmotionCode.FUNNY,
        EEmotionCode.SURPRISE,
        EEmotionCode.SADNESS
    );

    public static List<EEmotionCode> allowed(EmojiSetType type) {
        return switch (type) {
            case EMOTION -> EMOTION;
            case REACTION -> REACTION;
        };
    }
}
