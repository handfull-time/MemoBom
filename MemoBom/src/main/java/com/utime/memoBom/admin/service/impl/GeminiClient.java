package com.utime.memoBom.admin.service.impl;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.utime.memoBom.admin.vo.gemini.GeminiRequest;
import com.utime.memoBom.admin.vo.gemini.GeminiResponse;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GeminiClient {

	@Value("${env.gemini.key}")
	private String ApiKey;

	@Value("${env.gemini.version:gemini-2.0-flash}")
	private String ApiVersion;

	@Value("${env.proxy.address:localhost}")
	private String ProxyAddress;

	@Value("${env.proxy.port:8008}")
	private int ProxyPort;

	private HttpClient httpClient;
	
    @PostConstruct
    public void init() {
	    final String isProxy = System.getProperty("useProxy"); // 실행 옵션 -DuseProxy=true

	    HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10));

	    if ("true".equals(isProxy)) {
	        builder.proxy(ProxySelector.of(new InetSocketAddress(ProxyAddress, ProxyPort)));
	    }

        this.httpClient = builder.build();
    }
	
	private final ObjectMapper objectMapper = new ObjectMapper()
			.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

	public GeminiResponse call(String prompt, boolean isSearch) throws Exception {

		// 요청 구성 (AI Studio 예시와 동일한 contents/parts/text)
		final GeminiRequest.Part part = new GeminiRequest.Part();
		part.setText(prompt + "\n응답은 반드시 마크다운 없이 순수 JSON 형식으로만 해줘.");

		final GeminiRequest.Content content = new GeminiRequest.Content();
		content.setParts(List.of(part));

		final GeminiRequest req = new GeminiRequest();
		req.setContents(List.of(content));

		if( isSearch ) {
			// 구글 검색(Grounding) 활성화
			req.setTools(List.of(new GeminiRequest.Tool()));
		}
		

		// (선택) JSON 응답 강제: 모델이 JSON을 “형식적으로” 맞추도록 유도
		final GeminiRequest.GenerationConfig config = new GeminiRequest.GenerationConfig();
		if( ! isSearch ) {
			// google search 는 MimeType json을 지원하지 않음.
			config.setResponseMimeType("application/json");
		}
		config.setTemperature(0.7F);
		config.setMaxOutputTokens(1024);
		req.setGenerationConfig(config);

		final String body = objectMapper.writeValueAsString(req);
		log.info("Gemini Request : {}", body);

		final String endPoint = "https://generativelanguage.googleapis.com/v1beta/models/" + ApiVersion
				+ ":generateContent";

		final HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(endPoint))
				.header("Content-Type", "application/json").header("X-goog-api-key", ApiKey)
				.POST(HttpRequest.BodyPublishers.ofString(body)).build();

		final HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

		final String resBody = response.body();
		log.info("Gemini Response : {}", resBody);

		// 상태코드 체크 (권장)
		if (response.statusCode() / 100 != 2) {
			throw new RuntimeException("Gemini API error: " + response.statusCode() + " / " + resBody);
		}

		return objectMapper.readValue(resBody, GeminiResponse.class);
	}
}

/*
 * GeminiResponse response = geminiClient.callGemini( """ 다음 정보를 JSON으로 응답하라. {
 * "title": "string", "summary": "string" } 주제: Spring Security JWT """ );
 * 
 * String jsonText = response.getCandidates().get(0)
 * .getContent().getParts().get(0).getText();
 * 
 * // JSON 문자열 → 객체로 다시 변환 가능 MyResultVo result =
 * objectMapper.readValue(jsonText, MyResultVo.class);
 */
