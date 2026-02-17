package com.utime.memoBom.admin.service.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.Strictness;
import com.utime.memoBom.admin.vo.gemini.GeminiRequest;
import com.utime.memoBom.admin.vo.gemini.GeminiResponse;
import com.utime.memoBom.admin.vo.gemini.GeminiResponse.Candidate;
import com.utime.memoBom.admin.vo.gemini.GeminiResponse.Content;
import com.utime.memoBom.admin.vo.gemini.GeminiResponse.Part;
import com.utime.memoBom.common.util.AppUtils;

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

	@Value("${env.proxy.port:0}")
	private int ProxyPort;

	private HttpClient httpClient;
	
	private final Gson gson = new GsonBuilder()
			.setStrictness(Strictness.LENIENT) 
            .create();
	
    @PostConstruct
    public void init() {
	    final String isProxy = System.getProperty("useProxy"); // 실행 옵션 -DuseProxy=true

	    HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10));

	    if ("true".equals(isProxy) && ProxyPort != 0 ) {
	        builder.proxy(ProxySelector.of(new InetSocketAddress(ProxyAddress, ProxyPort)));
	    }

        this.httpClient = builder.build();
    }
	
	private final ObjectMapper objectMapper = new ObjectMapper()
			.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

	private String clearJson( String json ) {
		
		String cleanJson = json.trim();
		
		if (cleanJson.contains("{") || cleanJson.contains("[")) {
		    // 가장 처음 나타나는 { 또는 [ 부터 마지막 } 또는 ] 까지 추출
		    int start = Math.min(cleanJson.indexOf("{"), cleanJson.indexOf("["));
		    int end = Math.max(cleanJson.lastIndexOf("}"), cleanJson.lastIndexOf("]"));
		    if (start != -1 && end != -1 && start < end) {
		        cleanJson = cleanJson.substring(start, end + 1);
		    }
		}
		// 그 다음 기존의 replaceAll 수행
		cleanJson = cleanJson.replaceAll("^```json|```$", "").trim();
		
		return cleanJson;
	}
	
	public <T> List<T> call(String prompt, Class<T> classOfT, boolean isSearch) throws Exception {

		final T dummyInstance = classOfT.getDeclaredConstructor().newInstance();
		final String jsonFormat = objectMapper.writeValueAsString(dummyInstance);
		
		// 요청 구성 (AI Studio 예시와 동일한 contents/parts/text)
		final GeminiRequest.Part part = new GeminiRequest.Part();
		part.setText(prompt + "\n마크다운 코드 블록 안에 순수한 JSON 데이터만 출력해 줘. 그렇지 않으면 난 네 응답을 해석하지 못할꺼야. 응답 json 방식은 '" + jsonFormat + "'이다.");

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

		try {
			final GeminiResponse res = gson.fromJson(resBody, GeminiResponse.class);
			
			if( res == null ) {
				return null;
			}
			
			final List<Candidate> gCandidates = res.getCandidates();
			if( AppUtils.isEmpty(gCandidates)) {
				return null;
			}
			
			Candidate gCandidate = gCandidates.get(0);
			if( gCandidate == null ) {
				return null;
			}
			
			Content gContent = gCandidate.getContent();
			if( gContent == null ) {
				return null;
			}
			
			List<Part> parts =  gContent.getParts();
			if( AppUtils.isEmpty(parts)) {
				return null;
			}
			
			final String text = parts.get(0).getText();
			final String cleanJson = clearJson( text );
			log.info("Pure value\n{}", cleanJson);
			
			final Type typeOfList = new ParameterizedTypeImpl(List.class, new Type[]{classOfT});
			
			return gson.fromJson(cleanJson, typeOfList);
			
		} catch (Exception e) {
			log.error("E", e);
		}
		
		return null;
	}
	
	record ParameterizedTypeImpl(Type raw, Type[] args) implements ParameterizedType {
	    public Type[] getActualTypeArguments() { return args; }
	    public Type getRawType() { return raw; }
	    public Type getOwnerType() { return null; }
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
