package com.utime.memoBom.admin.vo.gemini;

import java.util.List;

import lombok.Data;

@Data
public class GeminiResponse {

    private List<Candidate> candidates;

    @Data
    public static class Candidate {
        private Content content;
    }

    @Data
    public static class Content {
        private List<Part> parts;
    }

    @Data
    public static class Part {
        private String text; // JSON 문자열
    }
}

