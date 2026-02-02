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

