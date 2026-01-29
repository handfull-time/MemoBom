package com.utime.memoBom.user.service.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.utime.memoBom.user.dao.HolidayDao;
import com.utime.memoBom.user.vo.HolidayVo;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 공공데이터 포털을 이용해 공휴일 정보 수집<br>
 * @link https://www.data.go.kr/data/15012690/openapi.do
 */
@Slf4j 
@Service
public class HolidayServiceImpl {
	
	@Value("${korean.dataio.key.SpcdeInfoService}")
	private String serviceKey;
	
	@Autowired
	private HolidayDao holidayDao;
	
//	@Autowired
//	private ObjectMapper mapper;
//	
//	private static class _Spcd{
//		public static class _Header{
//			public String resultCode;
//			public String resultMsg;
//		}
//		
//		public _Header header;
//		
//		public static class _Item{
//			public String dataKind;
//			public String dateName;
//			public String isHoliday;
//			public String locdate;
//			public int seq;
//		}
//		
//		public static class _Body{
//			public List<_Item> items;
//			public int numOfRows;
//			public int pageNo;
//			public int totalCount;
//		}
//		
//		public _Body body;
//	}
	
    /**
     * 특정 태그 사이의 값을 가져오는 헬퍼 메서드
     */
    private String getTagValue(String source, String tag) {
        String startTag = "<" + tag + ">";
        String endTag = "</" + tag + ">";

        int start = source.indexOf(startTag);
        if (start == -1) return null;

        int end = source.indexOf(endTag, start);
        if (end == -1) return null;

        return source.substring(start + startTag.length(), end);
    }
    
    /**
     * 단순 xml 분석은 걍 무식하게 처리하자. 그게 빠르다.
     * @param xml
     * @return
     */
    private List<HolidayVo> parseHolidayXml(String xml) {
    	
        final List<HolidayVo> result = new ArrayList<>();
        
        // 1. <items> 내부만 추출 (불필요한 헤더 제거)
        String itemsBlock = this.getTagValue(xml, "items");
        if (itemsBlock == null) return result;

        // 2. <item> 태그 단위로 분할하여 처리
        int lastPos = 0;
        while ((lastPos = itemsBlock.indexOf("<item>", lastPos)) != -1) {
        	final int endPos = itemsBlock.indexOf("</item>", lastPos);
            if (endPos == -1) break;

            // <item>...</item> 사이의 내용만 추출
            final String itemXml = itemsBlock.substring(lastPos + 6, endPos);
            
            // 3. 개별 데이터 추출 및 객체 매핑
            final HolidayVo data = new HolidayVo();
            data.setDateKind( Integer.parseInt(this.getTagValue(itemXml, "dateKind")) );
            data.setDateName( this.getTagValue(itemXml, "dateName") );
            data.setHoliday( "Y".equalsIgnoreCase(this.getTagValue(itemXml, "isHoliday")) );
            data.setLocdate( this.getTagValue(itemXml, "locdate") );

            result.add(data);
            
            // 다음 아이템 탐색을 위해 위치 이동
            lastPos = endPos + 7;
        }
        
        log.info("데이터 분석 결과 : {}" , result.size());

        return result;
    }
    
    public List<HolidayVo> getHolidayInfo(int year, String target) throws Exception {

        final URI uri = UriComponentsBuilder
                .fromUriString("http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/" + target)
                .queryParam("serviceKey", serviceKey)
                .queryParam("solYear", year)
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "120")
//                .queryParam("_type", "json") // json으로 받고 싶을 때.
                .build(true)
                .toUri();

        final HttpClient client = HttpClient.newBuilder()
        		.connectTimeout(Duration.ofSeconds(5))
        		.build();

        final HttpRequest request = HttpRequest.newBuilder(uri)
                .GET()
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/xml")
                .build();

        String resData;
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            log.info("Response code: " + response.statusCode());
            
            resData = response.body();
            log.info("Response data: " + resData );
		} catch (Exception e) {
			log.error("apis.data.go.kr", e);
			throw e;
		}
//        final _Spcd spcd = mapper.readValue(resData, _Spcd.class);
        
        return this.parseHolidayXml( resData );
    }
    
    /**
     * 각종 정보 종류
     */
    final String [] TargetUrl = new String [] {
    		// 기념일
    		"getAnniversaryInfo",
    		// 공휴일
    		"getRestDeInfo",
    		// 국경일
    		"getHoliDeInfo",
    		// 잡절
    		"getSundryDayInfo",
    		// 24절기
    		"get24DivisionsInfo"
    	};
    
	// 초 분 시 일 월 요일
    // 매년 1월 1일 01시 00분 00초에 실행
    @Scheduled(cron = "0 0 1 1 12 *")
    public void runHoliday() throws Exception {
    	
    	final Calendar cal = Calendar.getInstance(Locale.KOREAN);
    	cal.add( Calendar.MONTH, 1);
    	final int year = cal.get( Calendar.YEAR );
    	
    	//이미 데이터가 존재하면 패스.
    	if( holidayDao.hasHolidayList(year) ) {
			log.info( year + "년 DataIO 정보가 이미 존재합니다.");
			return;
		}
    	
    	final List<HolidayVo> holiList = new ArrayList<>();
    	
    	for( String tgarget : TargetUrl ) {
    		final List<HolidayVo> itemList = this.getHolidayInfo( year, tgarget);
    		holiList.addAll( itemList );
    	}
    	
    	//기념일 추가. 
    	final int addCount = holidayDao.insertHolidayList( holiList );
    	log.info( year + "년 정보 " + addCount + "건이 추가되었습니다.");
    }
    
    @PostConstruct
	private void init() throws Exception{
    	new Thread( new Runnable() {
    		
    		@Override
    		public void run() {
    			try {
					runHoliday();
				} catch (Exception e) {
					log.error("초기 DataIO 정보 수집 중 오류 발생 {}", e.getMessage());
				}    			
    		}
    	}).start();
    }
}
