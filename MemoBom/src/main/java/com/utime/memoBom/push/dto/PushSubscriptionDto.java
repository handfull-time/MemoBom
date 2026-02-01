package com.utime.memoBom.push.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Web Push Subscription 정보를 담는 DTO.
 *
 * <p>
 * 이 객체는 브라우저(Service Worker)의
 * {@code PushManager.subscribe()} 호출 결과를 그대로 전달받아
 * 서버에 저장하기 위한 데이터 구조이다.
 * </p>
 *
 * <p>
 * 로그인 사용자와 1:N 관계로 매핑되며,
 * 하나의 사용자가 여러 브라우저/기기에서 각각 다른
 * Push Subscription을 가질 수 있다.
 * </p>
 *
 * <p>
 * 해당 정보는 VAPID 기반 Web Push 발송 시
 * {@link nl.martijndwars.webpush.Notification} 객체 생성에 사용된다.
 * </p>
 *
 * <h3>클라이언트 전달 예시</h3>
 * <pre>
 * {
 *   "endpoint": "https://fcm.googleapis.com/fcm/send/...",
 *   "keys": {
 *     "p256dh": "BCVx...",
 *     "auth": "d0g..."
 *   }
 * }
 * </pre>
 *
 * @author openAI
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PushSubscriptionDto {

	long subNo;
	
    /**
     * Push 메시지를 전달받을 엔드포인트 URL.
     *
     * <p>
     * 브라우저 벤더(Chrome/Firefox/Safari 등)에서 제공하는
     * 고유 URL로, Push 메시지 발송 시 대상 주소로 사용된다.
     * </p>
     *
     * <p>
     * 일반적으로 동일한 endpoint는 하나의 브라우저/프로필/기기를
     * 식별하며, 서버에서는 중복 저장을 방지하기 위해
     * 유니크 키로 관리하는 것이 권장된다.
     * </p>
     */
    public String endpoint;

    /**
     * Web Push 암호화를 위한 키 정보.
     *
     * <p>
     * {@code p256dh}와 {@code auth} 값은 브라우저에서 생성되며,
     * 서버는 해당 값을 이용해 Push payload를 암호화한다.
     * </p>
     *
     * <p>
     * 이 키들은 공개 키 쌍에 해당하며, 개인정보가 아니지만
     * Push 발송에 필수적인 정보이므로 안전하게 저장해야 한다.
     * </p>
     */
    public Keys keys;

    /**
     * Web Push 암호화 키 묶음.
     */
    @Data
    public static class Keys {

        /**
         * P-256 ECDH 공개 키(Base64URL 인코딩).
         *
         * <p>
         * Push payload 암호화(ECDH)에 사용되는 공개 키로,
         * 브라우저에서 생성되며 Base64URL(패딩 없음) 형태로 전달된다.
         * </p>
         */
        public String p256dh;

        /**
         * 인증용 비밀 값(Base64URL 인코딩).
         *
         * <p>
         * 메시지 무결성과 수신자 인증을 위해 사용되는 값으로,
         * Push 서비스에서 payload 복호화 시 활용된다.
         * </p>
         */
        public String auth;
    }
    
 // optional
    private String isActive;
    private String deviceId;
    private String userAgent;
    private String browser;
    private String os;
    private LocalDateTime lastPushDate;
    private Integer failCount;
    private Long expirationTime;
}
