package com.utime.memoBom.push.controller;

import java.security.KeyFactory;
import java.security.interfaces.ECPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.push.dto.PushClickDto;
import com.utime.memoBom.push.dto.PushSubscriptionDto;
import com.utime.memoBom.push.service.PushSendService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("Push")
@RequiredArgsConstructor
public class PushController {

    @Value("${app.push.vapid.public-key}")
    private String vapidPublicKey;
    
    private final PushSendService pushSendService;
    

    /**
     * 키 전달
     * @return
     */
    @GetMapping("vapid-public-key.json")
    public ReturnBasic getVapidPublicKey() {
        // Front에 공개키 전달
        return new ReturnBasic(AppDefine.ERROR_OK, normalizePublicKeyForClient(vapidPublicKey));
    }

    private static String normalizePublicKeyForClient(String value) {
    	final byte[] decoded = decodeFlexibleBase64(value, "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----");

    	byte[] keyBytes = decoded;
    	if (!(decoded.length == 65 && decoded[0] == 0x04)) {
    		try {
    			final KeyFactory keyFactory = KeyFactory.getInstance("EC");
    			final ECPublicKey publicKey = (ECPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(decoded));

    			final byte[] x = fixedLength(publicKey.getW().getAffineX().toByteArray(), 32);
    			final byte[] y = fixedLength(publicKey.getW().getAffineY().toByteArray(), 32);
    			final byte[] raw = new byte[65];
    			raw[0] = 0x04;
    			System.arraycopy(x, 0, raw, 1, 32);
    			System.arraycopy(y, 0, raw, 33, 32);
    			keyBytes = raw;
    		} catch (Exception e) {
    			throw new IllegalArgumentException("invalid vapid public key format", e);
    		}
    	}

    	if (keyBytes.length != 65 || keyBytes[0] != 0x04) {
    		throw new IllegalArgumentException("invalid vapid public key. expected uncompressed EC point(65 bytes)");
    	}

    	return Base64.getUrlEncoder().withoutPadding().encodeToString(keyBytes);
    }

    private static byte[] fixedLength(byte[] value, int size) {
    	if (value.length == size) {
    		return value;
    	}

    	final byte[] out = new byte[size];
    	if (value.length > size) {
    		System.arraycopy(value, value.length - size, out, 0, size);
    	} else {
    		System.arraycopy(value, 0, out, size - value.length, value.length);
    	}
    	return out;
    }

    private static byte[] decodeFlexibleBase64(String value, String beginMarker, String endMarker) {
    	if (value == null) {
    		throw new IllegalArgumentException("vapid public key is null");
    	}

    	final String compact = value
    			.replace(beginMarker, "")
    			.replace(endMarker, "")
    			.replaceAll("\\s+", "");

    	if (compact.isEmpty()) {
    		throw new IllegalArgumentException("vapid public key is blank");
    	}

    	final String base64 = compact.replace('-', '+').replace('_', '/');
    	final int mod = base64.length() % 4;
    	final String padded = mod == 0 ? base64 : base64 + "=".repeat(4 - mod);
    	return Base64.getDecoder().decode(padded);
    }
    
    /**
     * 푸시 구독하기.
     * @param user
     * @param dto
     * @return
     * @throws Exception
     */
    @PostMapping("Subscription.json")
    public ResponseEntity<ReturnBasic> upsert(LoginUser user, @RequestBody PushSubscriptionDto dto ) throws Exception {
    	
        if (dto == null || dto.endpoint == null || dto.keys == null ||
            dto.keys.p256dh == null || dto.keys.auth == null) {
            return ResponseEntity.internalServerError().body( new ReturnBasic("E", "invalid subscription") );
        }
 
        final ReturnBasic res = pushSendService.upsert( user, dto );
        
        if( res.isError() ) {
        	return ResponseEntity.internalServerError().body(res);
        }else {
        	return ResponseEntity.ok().body(new ReturnBasic());
        }
    }

    /**
     * 푸시 구독 해제
     * @param user
     * @param endpoint
     * @return
     * @throws Exception
     */
    @DeleteMapping("Subscription.json")
    public ResponseEntity<ReturnBasic> delete(LoginUser user, @RequestParam String endpoint) throws Exception {
    
    	pushSendService.deleteAllByUserIdAndEndpoint(user, endpoint);
        
    	return ResponseEntity.ok().body(new ReturnBasic());
    }
    
    /**
     * 푸시 수신 상태
     * @param user
     * @return
     * @throws Exception
     */
    @GetMapping("Status.json")
    public ResponseEntity<ReturnBasic> getStatus(LoginUser user, @RequestParam String deviceId) throws Exception {
        
    	final ReturnBasic res = pushSendService.getPushStatus(user, deviceId);
        
    	return ResponseEntity.ok().body( res );
    }
    
    /**
     * 푸시 수신 설정
     * @param user
     * @param enabled true:수신, false:거부
     * @return
     * @throws Exception
     */
    @PostMapping("Status.json")
    public ResponseEntity<ReturnBasic> setStatus(LoginUser user, @RequestParam boolean enabled) throws Exception {
        
    	final ReturnBasic res = pushSendService.setPushStatus(user, enabled);
        
    	return ResponseEntity.ok().body( res );
    }
    
    /**
     * 클릭 여부 확인. 통계용.
     * @param dto
     * @return
     */
    @PostMapping("Click.json")
    public ResponseEntity<ReturnBasic> click(@RequestBody PushClickDto dto) {
    	
    	return ResponseEntity.ok().body( pushSendService.procClickEvent( dto.getClickId() ) );
    }
    
}
