package com.utime.memoBom.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.utime.memoBom.common.vo.EDevicePlatform;
import com.utime.memoBom.common.vo.UserDevice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppUtils {

	private static final String UnknownIp = "unknown";

	/**
	 * 접근한 IP Address를 반환한다.
	 * 
	 * @param request
	 * @return Real Remote Address
	 */
	public static String getRemoteAddress(final HttpServletRequest request) {

		String result = request.getHeader("X-Forwarded-For");
		if (result == null || result.length() == 0 || UnknownIp.equalsIgnoreCase(result)) {
			result = request.getHeader("Proxy-Client-IP");
		}
		if (result == null || result.length() == 0 || UnknownIp.equalsIgnoreCase(result)) {
			result = request.getHeader("WL-Proxy-Client-IP");
		}
		if (result == null || result.length() == 0 || UnknownIp.equalsIgnoreCase(result)) {
			result = request.getHeader("HTTP_CLIENT_IP");
		}
		if (result == null || result.length() == 0 || UnknownIp.equalsIgnoreCase(result)) {
			result = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (result == null || result.length() == 0 || UnknownIp.equalsIgnoreCase(result)) {
			result = request.getRemoteAddr();
		}

		if (result != null && result.indexOf(",") > 0) {
			// ELB 접근 했을 때와 EC2 접근 IP가 [,]를 구분으로 넘어 온다.
			result = result.split(",")[0];
		}

		return result;
	}

	/**
	 * obj 값이 비었는가?
	 * 
	 * @param obj
	 * @return true : null 또는 암것도 없다.
	 */
	public static boolean isEmpty(Object obj) {

		if (obj == null) return true;

	    // Optional 지원 (Modern Java)
	    if (obj instanceof Optional) return ((Optional<?>) obj).isEmpty();

	    // String 처리 (문자열 "null" 체크는 필요에 따라 제거/유지)
	    if (obj instanceof CharSequence) return obj.toString().trim().isEmpty();

	    // Collection & Map 처리
	    if (obj instanceof Collection) return ((Collection<?>) obj).isEmpty();
	    if (obj instanceof Map) return ((Map<?, ?>) obj).isEmpty();

	    // 배열 처리 (Primitive 배열까지 대응 가능)
	    if (obj.getClass().isArray()) {
	        return Array.getLength(obj) == 0;
	    }

	    // Number 처리 (0인 경우 비어있다고 정의할 경우)
	    if (obj instanceof Number) {
	        return ((Number) obj).doubleValue() == 0;
	    }

	    return false;
	}

	public static boolean isNotEmpty(Object obj) {
		return !AppUtils.isEmpty(obj);
	}

	/**
	 * 자동 봇인지 검사
	 * @param userAgent
	 * @return
	 */
	public static boolean isBot(String userAgent) {
		if( userAgent == null )
			return false;
		
		final String uaLower = userAgent.toLowerCase();
        return  uaLower.contains("slackbot") ||
                uaLower.contains("twitterbot") ||
                uaLower.contains("facebookexternalhit") ||
                uaLower.contains("discordbot") ||
                uaLower.contains("whatsapp") ||
                uaLower.contains("kakaotalk")
        ;
    }
    
	/**
	 * DMS (Degree Minute Second) 형식의 위도 또는 경도 문자열을 Decimal Degree 형식으로 변환합니다.
	 *
	 * @param dmsString DMS 형식의 문자열 (예: "37 33' 59.78\" N", "126 58' 41.23\" E")
	 * @return 변환된 Decimal Degree 값 (Double)
	 * @throws IllegalArgumentException DMS 문자열 형식이 올바르지 않은 경우 발생
	 */
	public static double dmsToDecimal(String dmsString) {

		if (AppUtils.isEmpty(dmsString)) {
			return 0D;
		}

		final String[] parts = dmsString.trim().split("\\s+");
		if (parts.length < 3) {
			log.warn("잘못된 DMS 형식: " + dmsString);
			return 0D;
		}

		double degrees;
		double minutes;
		double seconds;
		String direction = "";

		try {
			degrees = Double.parseDouble(parts[0]);
			minutes = Double.parseDouble(parts[1].replace("'", ""));
			seconds = Double.parseDouble(parts[2].replace("\"", ""));

			if (parts.length > 3) {
				direction = parts[3].toUpperCase();
			}
		} catch (NumberFormatException e) {
			log.warn("숫자 형식 오류: " + dmsString);
			return 0D;
		}

		double decimalDegrees = degrees + (minutes / 60.0) + (seconds / 3600.0);

		if (direction.equals("S") || direction.equals("W")) {
			decimalDegrees *= -1;
		} else if (!direction.isEmpty() && !direction.equals("N") && !direction.equals("E")) {
			log.warn("잘못된 방향 표시: " + direction);
			decimalDegrees = 0D;
		}

		return decimalDegrees;
	}

	/**
	 * Decimal Degree 형식의 위도 또는 경도를 DMS (Degree Minute Second) 형식의 문자열로 변환합니다.
	 *
	 * @param decimalDegree Decimal Degree 값
	 * @param isLatitude    위도인 경우 true, 경도인 경우 false
	 * @return DMS 형식의 문자열 (예: "37° 33' 59.78\" N", "126° 58' 41.23\" E")
	 */
	public static String decimalToDMS(double decimalDegree, boolean isLatitude) {
		int degrees = (int) decimalDegree;
		double remainingMinutes = (decimalDegree - degrees) * 60;
		int minutes = (int) remainingMinutes;
		double seconds = (remainingMinutes - minutes) * 60;

		String direction = "";
		if (isLatitude) {
			direction = (decimalDegree >= 0) ? "N" : "S";
		} else {
			direction = (decimalDegree >= 0) ? "E" : "W";
		}

		degrees = Math.abs(degrees);
		minutes = Math.abs(minutes);
		seconds = Math.abs(seconds);

		return String.format("%d° %d' %.2f\" %s", degrees, minutes, seconds, direction);
	}

	private static DateTimeFormatter formatterZoneOffset = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ssXXX");
	private static DateTimeFormatter formatterUtcTime = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ssX");
	private static DateTimeFormatter formatterBasic = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

	public static LocalDateTime convertToLocalDateTime(String input) {

		if (AppUtils.isEmpty(input)) {
			return null;
		}

		LocalDateTime result = null;

		try {
			if (input.matches(".*[+-]\\d{2}:\\d{2}")) {
				// Case 1: with zone offset (e.g., +09:00)
				result = OffsetDateTime.parse(input, formatterZoneOffset).toLocalDateTime();
			} else if (input.endsWith("Z")) {
				// Case 2: UTC time
				result = OffsetDateTime.parse(input, formatterUtcTime).toLocalDateTime();
			} else {
				// Case 3: basic local format
				result = LocalDateTime.parse(input, formatterBasic);
			}
		} catch (Exception e) {
			log.error("" + input, e);
		}

		return result;
	}

	public static Date convertToDate(String input) {

		final LocalDateTime dt = convertToLocalDateTime(input);
		if (dt == null) {
			return null;
		}

		return Date.from(dt.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDateTime convertToLocalDateTime(FileTime fileTime) {
		// 변환 과정: FileTime → Instant → LocalDateTime
		final LocalDateTime result = fileTime.toInstant().atZone(ZoneId.systemDefault()) // 시스템 시간대 적용
				.toLocalDateTime();

		return result;
	}

	public static byte[] encodeImageToByteArray(InputStream inputStream) throws IOException {
		if (inputStream == null || inputStream.available() < 1) {
			return null;
		}

		final byte[] imageBytes = StreamUtils.copyToByteArray(inputStream);
		if (imageBytes == null || imageBytes.length < 10) {
			return null;
		}

		return imageBytes;
	}

	public static final MediaType MEDIA_TYPE_SVG = new MediaType("image", "svg+xml");

	public static MediaType detectImageType(byte[] header) {

		if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF
				&& header[3] == (byte) 0xE0) {
			return MediaType.IMAGE_JPEG;
		}

		if (header[0] == (byte) 0x89 && header[1] == (byte) 0x50 && header[2] == (byte) 0x4E && header[3] == (byte) 0x47
				&& header[4] == (byte) 0x0D && header[5] == (byte) 0x0A && header[6] == (byte) 0x1A
				&& header[7] == (byte) 0x0A) {
			return MediaType.IMAGE_PNG;
		}

		if (header[0] == (byte) 0x3C && header[1] == (byte) 0x3F && header[2] == (byte) 0x78 && header[3] == (byte) 0x6D
				&& header[4] == (byte) 0x6C) {
			return MEDIA_TYPE_SVG;
		}

		return null;
	}

	private final static ObjectWriter objWirter;
	static {
		final ObjectMapper objMapper = new ObjectMapper();
		objMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 타입 지원 추가
		objWirter = objMapper.writerWithDefaultPrettyPrinter();
	}

	/**
	 * json 형태 출력
	 * 
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) {

		if (obj == null) {
			return "Is null.";
		}

		try {
			return obj.getClass().getSimpleName() + ": " + objWirter.writeValueAsString(obj) + "\n";
		} catch (Exception e) {
			log.error("", e);
			return "{}";
		}
	}

	public static void shutdownAndAwait(ExecutorService executor, long timeout, TimeUnit unit) {
		if (executor == null)
			return;

		// 1) 신규 작업 거부, 기존 작업은 마무리 시도
		executor.shutdown();

		try {
			if (!executor.awaitTermination(timeout, unit)) {
				// 2) 타임아웃 -> 강제 중단 시도
				var notStarted = executor.shutdownNow(); // 대기 큐에 남은 작업 반환
				log.warn("Executor force shutdown. notStarted={}", notStarted.size());

				// 3) 강제 중단 후에도 일정 시간 더 기다림
				if (!executor.awaitTermination(timeout, unit)) {
					log.error("Executor did not terminate after shutdownNow.");
				}
			}
		} catch (InterruptedException e) {
			// 현재 스레드가 인터럽트 됐으면 즉시 강제 종료로 전환 + 인터럽트 상태 복원
			log.warn("Interrupted while waiting termination. Forcing shutdownNow.", e);
			executor.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
	
	
    // 정규표현식 패턴을 static final로 선언하여 컴파일 비용 절감
    private static final Pattern ANDROID_MODEL_PATTERN = Pattern.compile(";\\s?([^;]+?)\\s?(?:Build/|\\)$)");
    private static final Pattern MACOS_PATTERN = Pattern.compile("Macintosh|Darwin");
    private static final Pattern WINDOWS_PATTERN = Pattern.compile("Windows");
    private static final Pattern IPHONE_PATTERN = Pattern.compile("iPhone");
    private static final Pattern IPAD_PATTERN = Pattern.compile("iPad|iPod");
    
	/**
     * User-Agent를 분석하여 디바이스 플랫폼과 모델명을 반환한다.
     *
     * @param userAgent HTTP 요청 헤더의 User-Agent 문자열
     * @return 분석된 UserDevice 객체 (NonNull)
     */
    public static UserDevice getDeviceInfoFromUserAgent(final String userAgent) {
        // 1. 방어적 코드: 유효성 검사
        if (!StringUtils.hasText(userAgent)) {
            return new UserDevice(EDevicePlatform.Unknown, null);
        }

        // 2. 주요 플랫폼별 분기 처리 (Early Return 패턴 적용)
        if (userAgent.contains("Android")) {
            return analyzeAndroid(userAgent);
        }
        
        if (IPHONE_PATTERN.matcher(userAgent).find()) {
            return new UserDevice(EDevicePlatform.ApplePhone, "iPhone");
        }
        
        if (IPAD_PATTERN.matcher(userAgent).find()) {
            return new UserDevice(EDevicePlatform.ApplePad, "iPad");
        }
        
        if (MACOS_PATTERN.matcher(userAgent).find()) {
            return new UserDevice(EDevicePlatform.Pc, "Macintosh");
        }
        
        if (WINDOWS_PATTERN.matcher(userAgent).find()) {
            return new UserDevice(EDevicePlatform.Pc, "Windows");
        }
        
        if (userAgent.contains("Linux") && !userAgent.contains("Android")) {
            return new UserDevice(EDevicePlatform.Pc, "Linux");
        }

        if (userAgent.contains("SmartTV") || userAgent.contains("TV")) {
            return new UserDevice(EDevicePlatform.SmartTV, "SmartTV");
        }

        // 3. Fallback: 기존 로직의 CFNetwork 처리 등 기타 케이스 대응
        if (userAgent.contains("CFNetwork") && userAgent.contains("Darwin")) {
             return new UserDevice(EDevicePlatform.ApplePhone, "AppleMobile");
        }

        return new UserDevice(EDevicePlatform.Unknown, null);
    }

    /**
     * 안드로이드 계열 디바이스 정밀 분석
     * <p>
     * Android User-Agent 포맷: "Mozilla/5.0 (Linux; Android 10; SM-G960F Build/QP1A...) ..."
     * </p>
     */
    private static UserDevice analyzeAndroid(final String userAgent) {
        final boolean isTablet = userAgent.contains("Tablet") || !userAgent.contains("Mobile");
        final EDevicePlatform platform = isTablet ? EDevicePlatform.AndroidPad : EDevicePlatform.AndroidPhone;
        
        String model = "Unknown Device";
        
        // 정규식을 사용하여 'Android 버전; 모델명 Build/' 패턴에서 모델명 추출 시도
        final Matcher matcher = ANDROID_MODEL_PATTERN.matcher(userAgent);
        if (matcher.find()) {
            // 그룹 1: 세미콜론과 Build/ 사이의 문자열 (모델명 추정)
            final String rawModel = matcher.group(1).trim();
            // 제조사 중복 제거 및 클렌징 로직 필요 시 여기에 추가
            model = rawModel;
        } else {
            // Regex 매칭 실패 시 단순 파싱 시도 (기존 로직의 안전한 대체)
            final int buildIndex = userAgent.indexOf("Build/");
            if (buildIndex > 0) {
                final int semiColonIndex = userAgent.lastIndexOf(";", buildIndex);
                if (semiColonIndex > -1 && semiColonIndex < buildIndex) {
                    model = userAgent.substring(semiColonIndex + 1, buildIndex).trim();
                }
            }
        }

        // 삼성/LG 태블릿 특수 모델명 처리 (SM-T, LM-T 등)
        if (model.startsWith("SM-T") || model.startsWith("LM-T")) {
             return new UserDevice(EDevicePlatform.AndroidPad, model);
        }

        return new UserDevice(platform, model);
    }
    
    public static boolean isAjaxRequest(HttpServletRequest req) {
        String ajaxHeader = req.getHeader("X-Requested-With");
        String contentType = req.getHeader("Content-Type");
        
        return "XMLHttpRequest".equals(ajaxHeader) 
                || (contentType != null && contentType.contains("application/json"))
                || req.getRequestURI().startsWith("/Api/"); // API 경로 예시
    }
}
