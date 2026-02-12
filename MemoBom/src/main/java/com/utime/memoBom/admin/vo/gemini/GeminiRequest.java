package com.utime.memoBom.admin.vo.gemini;

import java.util.List;

import lombok.Data;

@Data
public class GeminiRequest {
	private List<Content> contents;

	// 옵션: JSON 응답 강제하고 싶으면 사용
	private GenerationConfig generationConfig;

	private List<Tool> tools;

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
		// v1beta에서 보통 camelCase
		private String responseMimeType; // "application/json"
		private Float temperature;
		private Integer maxOutputTokens;
	}

	@Data
	public static class Tool {
		private GoogleSearch googleSearch = new GoogleSearch();
	}

	@Data
	public static class GoogleSearch {
	}
}
