package com.utime.memoBom.admin.vo.gemini;

import lombok.Data;
import java.util.List;

@Data
public class GeminiRequest {

    private List<Content> contents;
    private GenerationConfig generationConfig;

    @Data
    public static class Content {
        private List<Part> parts;
    }

    @Data
    public static class Part {
        private String text;
    }

    @Data
    public static class GenerationConfig {
        private String responseMimeType; // application/json
        private int temperature = 0;
        private int maxOutputTokens = 1024;
    }
}
