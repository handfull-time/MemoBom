package com.utime.memoBom.push.controller;

import java.util.Map;

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
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.push.dto.PushSubscriptionDto;
import com.utime.memoBom.push.service.PushSendService;
import com.utime.memoBom.user.vo.UserVo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("Push")
@RequiredArgsConstructor
public class PushController {

    @Value("${app.push.vapid.public-key}")
    private String vapidPublicKey;
    
    private final PushSendService pushSendService;
    

    @GetMapping("vapid-public-key")
    public ResponseEntity<String> getVapidPublicKey() {
        // Front에 공개키 전달
        return ResponseEntity.ok(vapidPublicKey);
    }
    
    // ✅ 로그인 사용자만 접근 (Security에서 보호)
    @PostMapping("subscription")
    public ResponseEntity<?> upsert(LoginUser user, @RequestBody PushSubscriptionDto dto ) throws Exception {
    	
        if (dto == null || dto.endpoint == null || dto.keys == null ||
            dto.keys.p256dh == null || dto.keys.auth == null) {
            return ResponseEntity.badRequest().body("invalid subscription");
        }

        final ReturnBasic res = pushSendService.upsert( user, dto );
        
        if( res.isError() ) {
        	return ResponseEntity.internalServerError().body(res.getCode() + "-" + res.getMessage());
        }else {
        	return ResponseEntity.ok().build();
        }
    }

    @DeleteMapping("subscription")
    public ResponseEntity<?> delete(UserVo user, @RequestParam String endpoint ) throws Exception {
        
    	pushSendService.deleteAllByUserIdAndEndpoint(user, endpoint);
        
        return ResponseEntity.ok().build();
    }
    
//    @PostMapping("unsubscribe")
//    public ResponseEntity<?> unsubscribe(@RequestBody Map<String, String> body) {
//        String endpoint = body.get("endpoint");
//        pushSubMapper.deactivateByEndpoint(endpoint);
//        return ResponseEntity.ok(Map.of("ok", true));
//    }
    
}


/*
// PushController.java
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/push")
public class PushController {

    private final PushProperties props;
    private final PushSubMapper pushSubMapper;
    private final WebPushService webPushService;

    @GetMapping("/vapidPublicKey")
    public ResponseEntity<String> vapidPublicKey() {
        return ResponseEntity.ok(props.getPublicKey());
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody PushSubscribeRequest req) {
        // TODO: 실제로는 로그인 사용자에서 userNo를 가져오세요.
        Long userNo = 1L;

        Map<String, Object> sub = req.subscription;
        String endpoint = (String) sub.get("endpoint");
        Map<String, String> keys = (Map<String, String>) sub.get("keys");
        String p256dh = keys.get("p256dh");
        String auth = keys.get("auth");
        Long expirationTime = sub.get("expirationTime") == null ? null : ((Number) sub.get("expirationTime")).longValue();

        String userAgent = req.extra == null ? null : (String) req.extra.get("userAgent");

        PushSubscriptionDto dto = PushSubscriptionDto.builder()
                .userNo(userNo)
                .endPoint(endpoint)
                .p256dh(p256dh)
                .auth(auth)
                .isActive("Y")
                .userAgent(userAgent)
                .expirationTime(expirationTime)
                .failCount(0)
                .build();

        PushSubscriptionDto exists = pushSubMapper.findByEndpoint(endpoint);
        if (exists == null) {
            pushSubMapper.insert(dto);
        } else {
            pushSubMapper.updateByEndpoint(dto);
        }

        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@RequestBody Map<String, String> body) {
        String endpoint = body.get("endpoint");
        pushSubMapper.deactivateByEndpoint(endpoint);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // 테스트 발송(본인에게)
    @PostMapping("/test")
    public ResponseEntity<?> testPush(@RequestBody Map<String, String> body) {
        Long userNo = 1L;

        var subs = pushSubMapper.findActiveByUserNo(userNo);
        for (var sub : subs) {
            try {
                webPushService.send(sub, Map.of(
                        "title", "테스트 알림",
                        "body", body.getOrDefault("body", "Hello PWA Push!"),
                        "url",  body.getOrDefault("url", "/")
                ));
                pushSubMapper.markSuccess(sub.getSubNo());
            } catch (Exception e) {
                // 410 Gone, 404 Not Found 등이 오면 구독 만료 가능성이 큼
                pushSubMapper.markFail(sub.getSubNo());
            }
        }

        return ResponseEntity.ok(Map.of("sentTo", subs.size()));
    }
}

*/