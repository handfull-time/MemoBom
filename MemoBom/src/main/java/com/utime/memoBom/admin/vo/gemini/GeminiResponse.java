package com.utime.memoBom.admin.vo.gemini;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
public class GeminiResponse {

	private List<Candidate> candidates;
	
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Candidate {
		private Content content;
		// 검색 출처 정보 추가
//		private GroundingMetadata groundingMetadata;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Content {
		private List<Part> parts;
		private String role;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Part {
		private String text;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Web {
		// 원문 주소
		private String uri;
		// 제목
		private String title;
	}
}