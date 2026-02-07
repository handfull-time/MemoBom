package com.utime.memoBom.push.controller;

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
    

    @GetMapping("vapid-public-key.json")
    public ReturnBasic getVapidPublicKey() {
        // Front에 공개키 전달
        return new ReturnBasic(AppDefine.ERROR_OK, vapidPublicKey);
    }
    
    // ✅ 로그인 사용자만 접근 (Security에서 보호)
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
    public ResponseEntity<ReturnBasic> getStatus(LoginUser user) throws Exception {
        
    	final ReturnBasic res = pushSendService.getPushStatus(user);
        
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
    public ResponseEntity<?> click(@RequestBody PushClickDto dto) {
    	log.info(dto.getClickId());
    	return ResponseEntity.ok().body( new ReturnBasic() );
    }
    
}