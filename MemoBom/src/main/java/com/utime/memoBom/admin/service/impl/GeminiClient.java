package com.utime.memoBom.admin.service.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utime.memoBom.admin.vo.gemini.GeminiRequest;
import com.utime.memoBom.admin.vo.gemini.GeminiResponse;

@Service
public class GeminiClient {

    private static final String API_KEY = "YOUR_API_KEY";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiResponse callGemini(String prompt) throws Exception {

        GeminiRequest request = this.buildRequest(prompt);
        String requestBody = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response =
                httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), GeminiResponse.class);
    }

    private GeminiRequest buildRequest(String prompt) {

        GeminiRequest.Part part = new GeminiRequest.Part();
        part.setText(prompt);

        GeminiRequest.Content content = new GeminiRequest.Content();
        content.setParts(List.of(part));

        GeminiRequest.GenerationConfig config = new GeminiRequest.GenerationConfig();
        config.setResponseMimeType("application/json");

        GeminiRequest request = new GeminiRequest();
        request.setContents(List.of(content));
        request.setGenerationConfig(config);

        return request;
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
