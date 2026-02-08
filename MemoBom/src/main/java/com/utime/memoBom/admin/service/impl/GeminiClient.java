package com.utime.memoBom.admin.service.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utime.memoBom.admin.vo.gemini.GeminiRequest;
import com.utime.memoBom.admin.vo.gemini.GeminiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GeminiClient {

	@Value("${env.gemini.key}")
    private String ApiKey;
	
	@Value("${env.gemini.version}")
    private String ApiVersion;
	
        private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiResponse call(String prompt) throws Exception {

        // 요청 구성 (AI Studio 예시와 동일한 contents/parts/text)
        GeminiRequest.Part part = new GeminiRequest.Part();
        part.setText(prompt);

        GeminiRequest.Content content = new GeminiRequest.Content();
        content.setParts(List.of(part));

        GeminiRequest req = new GeminiRequest();
        req.setContents(List.of(content));

        // (선택) JSON 응답 강제: 모델이 JSON을 “형식적으로” 맞추도록 유도
        GeminiRequest.GenerationConfig config = new GeminiRequest.GenerationConfig();
        config.setResponseMimeType("application/json");
        config.setTemperature(0);
        config.setMaxOutputTokens(1024);
        req.setGenerationConfig(config);

        final String body = objectMapper.writeValueAsString(req);
        
        final String endPoint = "https://generativelanguage.googleapis.com/v1beta/models/"+ ApiVersion +":generateContent";

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(endPoint))
                .header("Content-Type", "application/json")
                .header("X-goog-api-key", ApiKey) 
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        // 상태코드 체크 (권장)
        if (response.statusCode() / 100 != 2) {
            throw new RuntimeException("Gemini API error: " + response.statusCode() + " / " + response.body());
        }
        
        final String resBody = response.body();
        log.info("\n\n\n{}\n\n", resBody );

        return objectMapper.readValue(resBody, GeminiResponse.class);
    }
}

/*
GeminiResponse response = geminiClient.callGemini(
    """
    다음 정보를 JSON으로 응답하라.
    {
      "title": "string",
      "summary": "string"
    }
    주제: Spring Security JWT
    """
);

String jsonText =
    response.getCandidates().get(0)
            .getContent().getParts().get(0).getText();

// JSON 문자열 → 객체로 다시 변환 가능
MyResultVo result = objectMapper.readValue(jsonText, MyResultVo.class);
*/
