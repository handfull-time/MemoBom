package com.utime.memoBom.root.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.utime.memoBom.admin.service.impl.GeminiClient;
import com.utime.memoBom.admin.vo.gemini.GeminiResponse;
import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.common.security.JwtProvider;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.EJwtRole;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.push.service.PushSendService;
import com.utime.memoBom.push.vo.PushNotiDataVo;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.dto.MySearchDto;
import com.utime.memoBom.user.service.UserService;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("Test")
@RequiredArgsConstructor
public class TestController {
	
	private final TopicService topicService;
	
    private final JwtProvider jwtProvider;

    private final UserDao userDao;
    
    private final UserService userService;
    
    private final PushSendService pushSendService;
	
    @GetMapping("Login")
	public String testLogin() throws Exception {
    	return "Test/TestLogin";
    }
    
    @GetMapping("LoginGo")
	public String testLoginGo(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam() String id) throws Exception {

    	UserVo userVo = userDao.findById("localPc", id);
    	
    	if( userVo == null ) {
    		userVo = new UserVo();
    		userVo.setId(id);
    		userVo.setProvider("localPc");
    		userVo.setEnabled(true);
    		userVo.setEmail(id + "@Gmail.cococo" );
    		userVo.setRole(EJwtRole.User);
    		userVo.setNickname( "Tester-" + id );
    		userVo.setProfileUrl( "/MemoBom/images/profile-placeholder.svg" );
    		
    		log.info("사용자 추가 정보 : {}", userVo);
    		
    		userDao.addUser(userVo);
    	}
		
		try {
			ReturnBasic result = jwtProvider.procLogin(request, response, userVo);
			log.info(result.toString());
		} catch (Exception e) {
			log.error("", e);
			model.addAttribute("res", new ReturnBasic("E", e.getMessage()) );
			model.addAttribute(AppDefine.KeyShowFooter, false );
		    model.addAttribute(AppDefine.KeyLoadScript, false );
			return "Common/ErrorAlert";
		}

		return "redirect:/";
	}

	@GetMapping("Push.html")
	public String testPush(Model model) {
		model.addAttribute("assetVersion", AppDefine.AssetVersion );
		return "Test/TestPush";
	}

	@GetMapping("Layout")
	public String testLayout() {
		return "Test/TestLayout";
	}

	@GetMapping("TestView")
	public String test(Model model) {
	    model.addAttribute("showHeader", true);
	    model.addAttribute("showFooter", false);
		return "Test/Test";
	}

	@GetMapping("TestView2")
	public String test2(Model model) {
	    model.addAttribute("showHeader", true);
	    model.addAttribute("showFooter", false);
		return "Test/Test2";
	}

	@GetMapping("Topic")
	public String topic(Model model) {

		model.addAttribute("topic", topicService.loadTopic(null));
		model.addAttribute("user", new UserVo());
		
		return "Topic/TopicItem";
	}
	
	@Value("${korean.dataio.key.SpcdeInfoService}")
	String serviceKey;
	
	@ResponseBody	
	@GetMapping("key")
	public String key(Model model) {
//		https://www.data.go.kr/data/15012690/openapi.do
		
//		기념일 정보 조회
//		/getAnniversaryInfo
//		공휴일 정보 조회
//		/getRestDeInfo
//		국경일 정보조회
//		/getHoliDeInfo
//		24절기 정보 조회
//		/get24DivisionsInfo
//		잡절 정보 조회
//		/getSundryDayInfo
		String url = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo?serviceKey="+serviceKey+"&solYear=2026&numOfRows=100";
		// https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo?serviceKey=__key__&stdt=2026
		return url;
	}
	
	private LoginUser getLoginUser() {
		return new LoginUser(2L, "a383c637-2bc8-468c-b1a0-2c8b11660fa3", EJwtRole.User);
	}
	
	@ResponseBody
	@GetMapping(path = "MyMosaic.json")
    public ReturnBasic myMosaic( ) {
		
		final MySearchDto searchVo = new MySearchDto();
		
		return userService.getMyMosaicDataList( getLoginUser(), searchVo );
    }
	
	@ResponseBody
	@GetMapping(path = "Fragments.json")
    public ReturnBasic myFragments() {
		
		final MySearchDto searchVo = new MySearchDto();
		
		return userService.getMyFragmentsDataList( getLoginUser(), searchVo );
    }
	
	@ResponseBody
	@GetMapping(path = "MyComments.json")
    public ReturnBasic myComments( ) {
		final MySearchDto searchVo = new MySearchDto();
		
		return userService.getMyCommentsDataList( getLoginUser(), searchVo );
    }
	
	@ResponseBody
	@GetMapping(path = "Scrap.json")
    public ReturnBasic myScrap( ) {
		final MySearchDto searchVo = new MySearchDto();
		
		return userService.getMyScrapDataList( getLoginUser(), searchVo );
    }
	
	@ResponseBody
	@GetMapping("status.json")
    public ResponseEntity<ReturnBasic> getStatus() throws Exception {
        
    	final ReturnBasic res = pushSendService.getPushStatus(getLoginUser());
        
    	return ResponseEntity.ok().body( res );
    }
    
	@ResponseBody
    @PostMapping("status.json")
    public ResponseEntity<ReturnBasic> setStatus(@RequestParam boolean enabled) throws Exception {
        
    	final ReturnBasic res = pushSendService.setPushStatus(getLoginUser(), enabled);
        
    	return ResponseEntity.ok().body( res );
    }
	
	@ResponseBody
    @PostMapping("sendPush.json")
    public ReturnBasic sendPush() throws Exception {
		final PushNotiDataVo data = new PushNotiDataVo();
		
		data.setTitle("push go");
		data.setMessage("제발 되라.");
		data.setIcon("/MemoBom/images/profile-placeholder.svg");
		data.getData().setClickId(UUID.randomUUID().toString());
		data.getData().setUrl("/Fragment/index.html");
		
    	final ReturnBasic res = pushSendService.sendPush(getLoginUser(), data);
        
    	return res;
    }
	
	private final GeminiClient gc; 
	
	@ResponseBody
	@GetMapping("Gemini.json")
    public ReturnBasic questionGemini() throws Exception {
		String json = """
{
  "candidates": [
    {
      "content": {
        "parts": [
          {
            "text": "내용"
          }
        ],
        "role": "model"
      },
      "finishReason": "STOP",
      "index": 0,
      "groundingMetadata": {
        "searchEntryPoint": {
          "renderedContent": "\u003cstyle\u003e.container {  align-items: center;  border-radius: 8px;  display: flex;  font-family: Google Sans, Roboto, sans-serif;  font-size: 14px;  line-height: 20px;  padding: 8px 12px;}.chip {  display: inline-block;  border: solid 1px;  border-radius: 16px;  min-width: 14px;  padding: 5px 16px;  text-align: center;  user-select: none;  margin: 0 8px;  -webkit-tap-highlight-color: transparent;}.carousel {  overflow: auto;  scrollbar-width: none;  white-space: nowrap;  margin-right: -12px;}.headline {  display: flex;  margin-right: 4px;}.gradient-container {  position: relative;}.gradient {  position: absolute;  transform: translate(3px, -9px);  height: 36px;  width: 9px;}@media (prefers-color-scheme: light) {  .container {    background-color: #fafafa;    box-shadow: 0 0 0 1px #0000000f;  }  .headline-label {    color: #1f1f1f;  }  .chip {    background-color: #ffffff;    border-color: #d2d2d2;    color: #5e5e5e;    text-decoration: none;  }  .chip:hover {    background-color: #f2f2f2;  }  .chip:focus {    background-color: #f2f2f2;  }  .chip:active {    background-color: #d8d8d8;    border-color: #b6b6b6;  }  .logo-dark {    display: none;  }  .gradient {    background: linear-gradient(90deg, #fafafa 15%, #fafafa00 100%);  }}@media (prefers-color-scheme: dark) {  .container {    background-color: #1f1f1f;    box-shadow: 0 0 0 1px #ffffff26;  }  .headline-label {    color: #fff;  }  .chip {    background-color: #2c2c2c;    border-color: #3c4043;    color: #fff;    text-decoration: none;  }  .chip:hover {    background-color: #353536;  }  .chip:focus {    background-color: #353536;  }  .chip:active {    background-color: #464849;    border-color: #53575b;  }  .logo-light {    display: none;  }  .gradient {    background: linear-gradient(90deg, #1f1f1f 15%, #1f1f1f00 100%);  }}\u003c/style\u003e\u003cdiv class=\"container\"\u003e  \u003cdiv class=\"headline\"\u003e    \u003csvg class=\"logo-light\" width=\"18\" height=\"18\" viewBox=\"9 9 35 35\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\"\u003e      \u003cpath fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M42.8622 27.0064C42.8622 25.7839 42.7525 24.6084 42.5487 23.4799H26.3109V30.1568H35.5897C35.1821 32.3041 33.9596 34.1222 32.1258 35.3448V39.6864H37.7213C40.9814 36.677 42.8622 32.2571 42.8622 27.0064V27.0064Z\" fill=\"#4285F4\"/\u003e      \u003cpath fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M26.3109 43.8555C30.9659 43.8555 34.8687 42.3195 37.7213 39.6863L32.1258 35.3447C30.5898 36.3792 28.6306 37.0061 26.3109 37.0061C21.8282 37.0061 18.0195 33.9811 16.6559 29.906H10.9194V34.3573C13.7563 39.9841 19.5712 43.8555 26.3109 43.8555V43.8555Z\" fill=\"#34A853\"/\u003e      \u003cpath fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M16.6559 29.8904C16.3111 28.8559 16.1074 27.7588 16.1074 26.6146C16.1074 25.4704 16.3111 24.3733 16.6559 23.3388V18.8875H10.9194C9.74388 21.2072 9.06992 23.8247 9.06992 26.6146C9.06992 29.4045 9.74388 32.022 10.9194 34.3417L15.3864 30.8621L16.6559 29.8904V29.8904Z\" fill=\"#FBBC05\"/\u003e      \u003cpath fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M26.3109 16.2386C28.85 16.2386 31.107 17.1164 32.9095 18.8091L37.8466 13.8719C34.853 11.082 30.9659 9.3736 26.3109 9.3736C19.5712 9.3736 13.7563 13.245 10.9194 18.8875L16.6559 23.3388C18.0195 19.2636 21.8282 16.2386 26.3109 16.2386V16.2386Z\" fill=\"#EA4335\"/\u003e    \u003c/svg\u003e    \u003csvg class=\"logo-dark\" width=\"18\" height=\"18\" viewBox=\"0 0 48 48\" xmlns=\"http://www.w3.org/2000/svg\"\u003e      \u003ccircle cx=\"24\" cy=\"23\" fill=\"#FFF\" r=\"22\"/\u003e      \u003cpath d=\"M33.76 34.26c2.75-2.56 4.49-6.37 4.49-11.26 0-.89-.08-1.84-.29-3H24.01v5.99h8.03c-.4 2.02-1.5 3.56-3.07 4.56v.75l3.91 2.97h.88z\" fill=\"#4285F4\"/\u003e      \u003cpath d=\"M15.58 25.77A8.845 8.845 0 0 0 24 31.86c1.92 0 3.62-.46 4.97-1.31l4.79 3.71C31.14 36.7 27.65 38 24 38c-5.93 0-11.01-3.4-13.45-8.36l.17-1.01 4.06-2.85h.8z\" fill=\"#34A853\"/\u003e      \u003cpath d=\"M15.59 20.21a8.864 8.864 0 0 0 0 5.58l-5.03 3.86c-.98-2-1.53-4.25-1.53-6.64 0-2.39.55-4.64 1.53-6.64l1-.22 3.81 2.98.22 1.08z\" fill=\"#FBBC05\"/\u003e      \u003cpath d=\"M24 14.14c2.11 0 4.02.75 5.52 1.98l4.36-4.36C31.22 9.43 27.81 8 24 8c-5.93 0-11.01 3.4-13.45 8.36l5.03 3.85A8.86 8.86 0 0 1 24 14.14z\" fill=\"#EA4335\"/\u003e    \u003c/svg\u003e    \u003cdiv class=\"gradient-container\"\u003e\u003cdiv class=\"gradient\"\u003e\u003c/div\u003e\u003c/div\u003e  \u003c/div\u003e  \u003cdiv class=\"carousel\"\u003e    \u003ca class=\"chip\" href=\"https://vertexaisearch.cloud.google.com/grounding-api-redirect/AUZIYQF7NWcwE067Cgoimd8cE715iuW7LE_kmV8ZBEPIhq_TsnUOs1-vC7oXD8snGqAtIEC0q9GuHNkTmKdBU21Krq5MAtlpt7zn2-_Py_Q63Le1IcI4SKL9zh_8tqbNsAQQlQnmVaclF3WwfxfIsyJoI8yRBGqhkdku43ZwKaaajUG53c-Pk-NI3CUhmnvc87UZ9S2coVczp3hyc53r4QSH0F35CfppLZ8DOOdfraBADRIpHcZJ9hmYIpd-fQUoig==\"\u003e오늘의 탑 뉴스\u003c/a\u003e    \u003ca class=\"chip\" href=\"https://vertexaisearch.cloud.google.com/grounding-api-redirect/AUZIYQE1hfoar4fqnnsR0jA1Tetc2qwOQvbNpfLL8WIBY6lovkA7trY1jkUUg5qYvwrM3iiRcD-1b-w3A1YDtvVZijxwK-cAsNNBD-d5iyhExh5hTGiqhLe3KXjtxWgFVrRbcT8ShdX_50sEowk2UO4B3_kswynUVhqT3IKTueTceAtPIXUOJwy9uO9tssUhbuHoK2t3Gc6Y1sYkMsDDtgDDcrHwnVM-sDdQdjpLm5gZmIliUlilA_G8r6SFDt4Yfy27R_8AC_UCTfOkRxWQOuZiKZ4FMCQv1xOjbwsuK3z05ecm-L2fHAKVTpFwqPg=\"\u003e오늘의 주요 뉴스 2026년 2월 12일\u003c/a\u003e    \u003ca class=\"chip\" href=\"https://vertexaisearch.cloud.google.com/grounding-api-redirect/AUZIYQH8uBHZoVcnpiJdopVYmMFuV2iJMjNn5QdRUvxj5foziIHcazTJz379xL5AoF-TQa1I18LduZA7x2dqmvuz_QU5UfwKTP9rL1seHxsIxt8OEneCYCn7b7LL8HaVwCLrbQlsr1GkxAy13IOD48wHOqfrE3ITFWYsByxKv8gVu0VRI7h8bla-1j70SHfrsXcqTABf6A73VkVGSkiKwwz3PCh7fgjQTcvor5r-yJtMrHX-5kbz1xgqOAQKQnI7j4_IcalOZg-8G0djc15NqbJEWpeLG9cXtfeHtxpYZMQ3oEbi7O0x4amkhqpZ-6qiA0YMIVQAhLE=\"\u003e2026년 2월 12일 중요 뉴스 헤드라인\u003c/a\u003e  \u003c/div\u003e\u003c/div\u003e"
        },
        "webSearchQueries": [
          "오늘의 주요 뉴스 2026년 2월 12일",
          "2026년 2월 12일 중요 뉴스 헤드라인",
          "오늘의 탑 뉴스"
        ]
      }
    }
  ],
  "usageMetadata": {
    "promptTokenCount": 32,
    "candidatesTokenCount": 124,
    "totalTokenCount": 825,
    "promptTokensDetails": [
      {
        "modality": "TEXT",
        "tokenCount": 32
      }
    ],
    "toolUsePromptTokenCount": 176,
    "toolUsePromptTokensDetails": [
      {
        "modality": "TEXT",
        "tokenCount": 176
      }
    ],
    "thoughtsTokenCount": 493
  },
  "modelVersion": "gemini-2.5-flash",
  "responseId": "ZzmNaY3AIcfh2roPg-mTwQ8"
}
""";
		final ObjectMapper objectMapper = JsonMapper.builder()
			    .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS) // 제어 문자 허용 설정
			    .build()
			    .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
			    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		GeminiResponse res = objectMapper.readValue(json, GeminiResponse.class);
		
//		
//		final GeminiResponse response = gc.call("오늘의 꼭 중요한 뉴스 2개 타이틀만 알려줘.", true);
//
//		log.info(response.toString());
		
		return new ReturnBasic("0", "ok");
	}

}
//
//
//
//curl "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent" \
//-H 'Content-Type: application/json' \
//-H 'X-goog-api-key: AIzaSyAA5mtwxTKqhvy-7DvPmUXxqAhee9zMH_I' \
//-X POST \
//-d '{
//  "contents": [
//    {
//      "parts": [
//        {
//          "text": "Explain how AI works in a few words"
//        }
//      ]
//    }
//  ]
//}'