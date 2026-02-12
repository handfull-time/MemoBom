package com.utime.memoBom.admin.vo.gemini;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

@Data
public class GeminiResponse {

	private List<Candidate> candidates;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Candidate {
		private Content content;
		// 검색 출처 정보 추가
		private GroundingMetadata groundingMetadata;
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
		@JsonDeserialize(using = RawJsonDeserializer.class)
		private String text;
		@JsonDeserialize(using = RawJsonDeserializer.class)
		private String json;
	}
	
	static class RawJsonDeserializer extends JsonDeserializer<String> {
	    @Override
	    public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
	        JsonNode node = jp.getCodec().readTree(jp);
	        return node.toString(); // 객체 형태를 문자열로 직렬화하여 반환
	    }
	}

	// --- Google Search 출처 확인을 위한 DTO ---
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class GroundingMetadata {
		private List<GroundingChunk> groundingChunks;
		// AI가 실제로 검색한 쿼리들
		private List<String> webSearchQueries;
		private SearchEntryPoint searchEntryPoint;
	}
	
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SearchEntryPoint {
		@JsonDeserialize(using = RawJsonDeserializer.class)
	    private String renderedContent;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class GroundingChunk {
		private Web web;
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