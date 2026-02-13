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
	
	private static final Gson gson = new GsonBuilder()
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
		part.setText(prompt + "\n응답은 반드시 마크다운 없이 Pure JSON 형식으로만 해줘. 그렇지 않으면 난 네 응답을 해석하지 못할꺼야. 응답 json 방식은 '" + jsonFormat + "'이다.");

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
		
//		final String resBody = "{\r\n"
//				+ "  \"candidates\": [\r\n"
//				+ "    {\r\n"
//				+ "      \"content\": {\r\n"
//				+ "        \"parts\": [\r\n"
//				+ "          {\r\n"
//				+ "            \"text\": \"```json\\n[\\n  {\\n    \\\"uri\\\": \\\"https://www.yna.co.kr/view/AKR20260213056151504\\\",\\n    \\\"title\\\": \\\"국방부, '계엄 관련 의혹' 강동길 해군참모총장 직무배제\\\"\\n  },\\n  {\\n    \\\"uri\\\": \\\"https://www.yna.co.kr/view/AKR20260213006151007\\\",\\n    \\\"title\\\": \\\"최가온, 2026 밀라노·코르티나담페초 동계 올림픽 스노보드 여자 하프파이프 결선에서 금메달 획득\\\"\\n  }\\n]\\n```\"\r\n"
//				+ "          }\r\n"
//				+ "        ],\r\n"
//				+ "        \"role\": \"model\"\r\n"
//				+ "      },\r\n"
//				+ "      \"finishReason\": \"STOP\",\r\n"
//				+ "      \"index\": 0,\r\n"
//				+ "      \"groundingMetadata\": {\r\n"
//				+ "        \"searchEntryPoint\": {\r\n"
//				+ "          \"renderedContent\": \"\\u003cstyle\\u003e\\n.container {\\n  align-items: center;\\n  border-radius: 8px;\\n  display: flex;\\n  font-family: Google Sans, Roboto, sans-serif;\\n  font-size: 14px;\\n  line-height: 20px;\\n  padding: 8px 12px;\\n}\\n.chip {\\n  display: inline-block;\\n  border: solid 1px;\\n  border-radius: 16px;\\n  min-width: 14px;\\n  padding: 5px 16px;\\n  text-align: center;\\n  user-select: none;\\n  margin: 0 8px;\\n  -webkit-tap-highlight-color: transparent;\\n}\\n.carousel {\\n  overflow: auto;\\n  scrollbar-width: none;\\n  white-space: nowrap;\\n  margin-right: -12px;\\n}\\n.headline {\\n  display: flex;\\n  margin-right: 4px;\\n}\\n.gradient-container {\\n  position: relative;\\n}\\n.gradient {\\n  position: absolute;\\n  transform: translate(3px, -9px);\\n  height: 36px;\\n  width: 9px;\\n}\\n@media (prefers-color-scheme: light) {\\n  .container {\\n    background-color: #fafafa;\\n    box-shadow: 0 0 0 1px #0000000f;\\n  }\\n  .headline-label {\\n    color: #1f1f1f;\\n  }\\n  .chip {\\n    background-color: #ffffff;\\n    border-color: #d2d2d2;\\n    color: #5e5e5e;\\n    text-decoration: none;\\n  }\\n  .chip:hover {\\n    background-color: #f2f2f2;\\n  }\\n  .chip:focus {\\n    background-color: #f2f2f2;\\n  }\\n  .chip:active {\\n    background-color: #d8d8d8;\\n    border-color: #b6b6b6;\\n  }\\n  .logo-dark {\\n    display: none;\\n  }\\n  .gradient {\\n    background: linear-gradient(90deg, #fafafa 15%, #fafafa00 100%);\\n  }\\n}\\n@media (prefers-color-scheme: dark) {\\n  .container {\\n    background-color: #1f1f1f;\\n    box-shadow: 0 0 0 1px #ffffff26;\\n  }\\n  .headline-label {\\n    color: #fff;\\n  }\\n  .chip {\\n    background-color: #2c2c2c;\\n    border-color: #3c4043;\\n    color: #fff;\\n    text-decoration: none;\\n  }\\n  .chip:hover {\\n    background-color: #353536;\\n  }\\n  .chip:focus {\\n    background-color: #353536;\\n  }\\n  .chip:active {\\n    background-color: #464849;\\n    border-color: #53575b;\\n  }\\n  .logo-light {\\n    display: none;\\n  }\\n  .gradient {\\n    background: linear-gradient(90deg, #1f1f1f 15%, #1f1f1f00 100%);\\n  }\\n}\\n\\u003c/style\\u003e\\n\\u003cdiv class=\\\"container\\\"\\u003e\\n  \\u003cdiv class=\\\"headline\\\"\\u003e\\n    \\u003csvg class=\\\"logo-light\\\" width=\\\"18\\\" height=\\\"18\\\" viewBox=\\\"9 9 35 35\\\" fill=\\\"none\\\" xmlns=\\\"http://www.w3.org/2000/svg\\\"\\u003e\\n      \\u003cpath fill-rule=\\\"evenodd\\\" clip-rule=\\\"evenodd\\\" d=\\\"M42.8622 27.0064C42.8622 25.7839 42.7525 24.6084 42.5487 23.4799H26.3109V30.1568H35.5897C35.1821 32.3041 33.9596 34.1222 32.1258 35.3448V39.6864H37.7213C40.9814 36.677 42.8622 32.2571 42.8622 27.0064V27.0064Z\\\" fill=\\\"#4285F4\\\"/\\u003e\\n      \\u003cpath fill-rule=\\\"evenodd\\\" clip-rule=\\\"evenodd\\\" d=\\\"M26.3109 43.8555C30.9659 43.8555 34.8687 42.3195 37.7213 39.6863L32.1258 35.3447C30.5898 36.3792 28.6306 37.0061 26.3109 37.0061C21.8282 37.0061 18.0195 33.9811 16.6559 29.906H10.9194V34.3573C13.7563 39.9841 19.5712 43.8555 26.3109 43.8555V43.8555Z\\\" fill=\\\"#34A853\\\"/\\u003e\\n      \\u003cpath fill-rule=\\\"evenodd\\\" clip-rule=\\\"evenodd\\\" d=\\\"M16.6559 29.8904C16.3111 28.8559 16.1074 27.7588 16.1074 26.6146C16.1074 25.4704 16.3111 24.3733 16.6559 23.3388V18.8875H10.9194C9.74388 21.2072 9.06992 23.8247 9.06992 26.6146C9.06992 29.4045 9.74388 32.022 10.9194 34.3417L15.3864 30.8621L16.6559 29.8904V29.8904Z\\\" fill=\\\"#FBBC05\\\"/\\u003e\\n      \\u003cpath fill-rule=\\\"evenodd\\\" clip-rule=\\\"evenodd\\\" d=\\\"M26.3109 16.2386C28.85 16.2386 31.107 17.1164 32.9095 18.8091L37.8466 13.8719C34.853 11.082 30.9659 9.3736 26.3109 9.3736C19.5712 9.3736 13.7563 13.245 10.9194 18.8875L16.6559 23.3388C18.0195 19.2636 21.8282 16.2386 26.3109 16.2386V16.2386Z\\\" fill=\\\"#EA4335\\\"/\\u003e\\n    \\u003c/svg\\u003e\\n    \\u003csvg class=\\\"logo-dark\\\" width=\\\"18\\\" height=\\\"18\\\" viewBox=\\\"0 0 48 48\\\" xmlns=\\\"http://www.w3.org/2000/svg\\\"\\u003e\\n      \\u003ccircle cx=\\\"24\\\" cy=\\\"23\\\" fill=\\\"#FFF\\\" r=\\\"22\\\"/\\u003e\\n      \\u003cpath d=\\\"M33.76 34.26c2.75-2.56 4.49-6.37 4.49-11.26 0-.89-.08-1.84-.29-3H24.01v5.99h8.03c-.4 2.02-1.5 3.56-3.07 4.56v.75l3.91 2.97h.88z\\\" fill=\\\"#4285F4\\\"/\\u003e\\n      \\u003cpath d=\\\"M15.58 25.77A8.845 8.845 0 0 0 24 31.86c1.92 0 3.62-.46 4.97-1.31l4.79 3.71C31.14 36.7 27.65 38 24 38c-5.93 0-11.01-3.4-13.45-8.36l.17-1.01 4.06-2.85h.8z\\\" fill=\\\"#34A853\\\"/\\u003e\\n      \\u003cpath d=\\\"M15.59 20.21a8.864 8.864 0 0 0 0 5.58l-5.03 3.86c-.98-2-1.53-4.25-1.53-6.64 0-2.39.55-4.64 1.53-6.64l1-.22 3.81 2.98.22 1.08z\\\" fill=\\\"#FBBC05\\\"/\\u003e\\n      \\u003cpath d=\\\"M24 14.14c2.11 0 4.02.75 5.52 1.98l4.36-4.36C31.22 9.43 27.81 8 24 8c-5.93 0-11.01 3.4-13.45 8.36l5.03 3.85A8.86 8.86 0 0 1 24 14.14z\\\" fill=\\\"#EA4335\\\"/\\u003e\\n    \\u003c/svg\\u003e\\n    \\u003cdiv class=\\\"gradient-container\\\"\\u003e\\u003cdiv class=\\\"gradient\\\"\\u003e\\u003c/div\\u003e\\u003c/div\\u003e\\n  \\u003c/div\\u003e\\n  \\u003cdiv class=\\\"carousel\\\"\\u003e\\n    \\u003ca class=\\\"chip\\\" href=\\\"https://vertexaisearch.cloud.google.com/grounding-api-redirect/AUZIYQGfGwBpo0vfWMT7f8y5IX211qPyLu6D7FVeIE6W_S0ccIDxB9TpubBtfzOejspQFW1OB5-phEw2GmcyNKFyNfRc1hvw6vD1HZve62j8YmI_2Hjqq4D_ROFnjoHlxygsjnGbIvMgIhpBx5QJd7dEpO-j9fAEVfCxwQdlcV0tmGmotuMlsF7u7sXK2EUAxsHnlEiKOImB1-pg9NnswxZmYNXdUf81VPydOB_lF16hYRcsI0aBsa-xRu_eLvnZMquD7DGIQpWdo7_XLs6L0ysvXg==\\\"\\u003e최신 뉴스 2026년 2월 13일\\u003c/a\\u003e\\n    \\u003ca class=\\\"chip\\\" href=\\\"https://vertexaisearch.cloud.google.com/grounding-api-redirect/AUZIYQFyjg5ayUUuse9lzD3paHRkJFpf4WD00km1jgAac7q41aTOeU5MgaOI6TJUm4bGloYqP7yi5TbAOj4Z3PunWW5tkdBmAJAdl8TDrzHOhcL-OI0IzFHUwCRTmb0si5zw86obdk3YolfZ1J11M04BdYkd1fCTVvKUnJVzEM--CJmzJbswVA55SnAQ0k0HBBfKfaMG-JtHxAzI2LFhj6PEIwaelBn8rax7RGAWitXYov5UttkJZZAwUPmkQCP4xvAlJIJu2JJAfTSEh-zyCjVkn8wpoGXeI5NbRINSfDxdzfykq6cs-hobsR3UNaM=\\\"\\u003e오늘의 주요 뉴스 2026년 2월 13일\\u003c/a\\u003e\\n    \\u003ca class=\\\"chip\\\" href=\\\"https://vertexaisearch.cloud.google.com/grounding-api-redirect/AUZIYQEUiju34JBu6RiHEuzrpfxR0WupEGMiBH_DNC17nkCWSSEiW6nDw5sftPB50qWAvmx4Rrm0CVueYQJaUWb_ZFEMEEA_gHIi6qguyiEfkCDjhUOOnqmiIqx24ugQhnU3GDrKBtSl18w69QrAB3qjLiuU7pAvoOtxPfWKU33nclfA2mGAMZkkevtjt4qpwL14aylQLeWFxNncIHba-MjvGQWF_4ENW5qUgKNg9E0_4SO9NqZq9BaHqApDgO19PXpYj8qNFYfbRe4eJN9-YSl6KSUIdg9Uy5bdXoW1VV9ko4E8nw==\\\"\\u003e주요 헤드라인 2026년 2월 13일\\u003c/a\\u003e\\n  \\u003c/div\\u003e\\n\\u003c/div\\u003e\\n\"\r\n"
//				+ "        },\r\n"
//				+ "        \"groundingChunks\": [\r\n"
//				+ "          {\r\n"
//				+ "            \"web\": {\r\n"
//				+ "              \"uri\": \"https://vertexaisearch.cloud.google.com/grounding-api-redirect/AUZIYQHxXXVUg3Bv__ysbH1X3RsGFIShin5NOwpfatlLZBooE2EcHg9q8LFCZCnB4FZ_Q7cnOIvtGpogxAPEhXuYmdPJAqZTBz_EpAgeDWXCc5DZ4-xh2dU-QMhTwjzXyTwIvZpsO-yFpgas5Djc\",\r\n"
//				+ "              \"title\": \"yna.co.kr\"\r\n"
//				+ "            }\r\n"
//				+ "          },\r\n"
//				+ "          {\r\n"
//				+ "            \"web\": {\r\n"
//				+ "              \"uri\": \"https://vertexaisearch.cloud.google.com/grounding-api-redirect/AUZIYQFVL7wep9FxiewQdN1TYESnjqvEMaWNeaziVhBX9iI85d8aeupq3au2EMUo9BNiL8Rf9SHiCHQ6nO4zKczTPPj18xhBqcuquC0qJHJJch3NCH-46h9XeSrSeuelAsfoN-WoVVTDCGsGwKwl\",\r\n"
//				+ "              \"title\": \"yna.co.kr\"\r\n"
//				+ "            }\r\n"
//				+ "          }\r\n"
//				+ "        ],\r\n"
//				+ "        \"groundingSupports\": [\r\n"
//				+ "          {\r\n"
//				+ "            \"segment\": {\r\n"
//				+ "              \"endIndex\": 165,\r\n"
//				+ "              \"text\": \"```json\\n[\\n  {\\n    \\\"uri\\\": \\\"https://www.yna.co.kr/view/AKR20260213056151504\\\",\\n    \\\"title\\\": \\\"국방부, '계엄 관련 의혹' 강동길 해군참모총장 직무배제\"\r\n"
//				+ "            },\r\n"
//				+ "            \"groundingChunkIndices\": [\r\n"
//				+ "              0\r\n"
//				+ "            ]\r\n"
//				+ "          },\r\n"
//				+ "          {\r\n"
//				+ "            \"segment\": {\r\n"
//				+ "              \"startIndex\": 165,\r\n"
//				+ "              \"endIndex\": 383,\r\n"
//				+ "              \"text\": \"\\\"\\n  },\\n  {\\n    \\\"uri\\\": \\\"https://www.yna.co.kr/view/AKR20260213006151007\\\",\\n    \\\"title\\\": \\\"최가온, 2026 밀라노·코르티나담페초 동계 올림픽 스노보드 여자 하프파이프 결선에서 금메달 획득\"\r\n"
//				+ "            },\r\n"
//				+ "            \"groundingChunkIndices\": [\r\n"
//				+ "              1\r\n"
//				+ "            ]\r\n"
//				+ "          }\r\n"
//				+ "        ],\r\n"
//				+ "        \"webSearchQueries\": [\r\n"
//				+ "          \"오늘의 주요 뉴스 2026년 2월 13일\",\r\n"
//				+ "          \"최신 뉴스 2026년 2월 13일\",\r\n"
//				+ "          \"주요 헤드라인 2026년 2월 13일\"\r\n"
//				+ "        ]\r\n"
//				+ "      }\r\n"
//				+ "    }\r\n"
//				+ "  ],\r\n"
//				+ "  \"usageMetadata\": {\r\n"
//				+ "    \"promptTokenCount\": 58,\r\n"
//				+ "    \"candidatesTokenCount\": 243,\r\n"
//				+ "    \"totalTokenCount\": 1341,\r\n"
//				+ "    \"promptTokensDetails\": [\r\n"
//				+ "      {\r\n"
//				+ "        \"modality\": \"TEXT\",\r\n"
//				+ "        \"tokenCount\": 58\r\n"
//				+ "      }\r\n"
//				+ "    ],\r\n"
//				+ "    \"toolUsePromptTokenCount\": 245,\r\n"
//				+ "    \"toolUsePromptTokensDetails\": [\r\n"
//				+ "      {\r\n"
//				+ "        \"modality\": \"TEXT\",\r\n"
//				+ "        \"tokenCount\": 245\r\n"
//				+ "      }\r\n"
//				+ "    ],\r\n"
//				+ "    \"thoughtsTokenCount\": 795\r\n"
//				+ "  },\r\n"
//				+ "  \"modelVersion\": \"gemini-2.5-flash\",\r\n"
//				+ "  \"responseId\": \"seKOaa3zJbuZ1e8PxeO1oAE\"\r\n"
//				+ "}";

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
