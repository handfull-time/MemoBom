package com.utime.memoBom.push.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.push.dao.PushSubscriptionDao;
import com.utime.memoBom.push.dto.PushSubscriptionDto;
import com.utime.memoBom.push.service.PushSendService;
import com.utime.memoBom.push.vo.PushNotiDataVo;
import com.utime.memoBom.push.vo.PushSendResVo;
import com.utime.memoBom.push.vo.PushSubVo;
import com.utime.memoBom.push.vo.query.PushSubInfoVo;
import com.utime.memoBom.user.dao.UserDao;

import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

@Slf4j
@Service
class PushSendServiceImpl implements PushSendService {

	private final PushSubscriptionDao pushDao;
    private final PushService pushService;
    private final UserDao userDao;
    
    @Value("${appName}")
	private String appName;
    
    @Autowired
    private ObjectMapper objMapper;
    
    public PushSendServiceImpl(
    		PushSubscriptionDao repo,
    		UserDao userDao,
            @Value("${app.push.vapid.public-key}") String publicKey,
            @Value("${app.push.vapid.private-key}") String privateKey,
            @Value("${app.push.vapid.subject}") String subject) throws Exception {

        this.pushDao = repo;
        this.userDao = userDao;

        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        this.pushService = new PushService(publicKey, privateKey, subject);
    }

    @Override
    public ReturnBasic upsert(LoginUser user, PushSubscriptionDto dto) {
    	
    	final PushSubVo vo = new PushSubVo();

        vo.setUserNo(user.userNo());
        vo.setEndPoint(dto.endpoint);
        vo.setP256dh(dto.keys.p256dh);
        vo.setAuth(dto.keys.auth);
        vo.setExpirationTime(dto.expirationTime != null ? dto.expirationTime : 0L);

        vo.setActive(true);
        vo.setDeviceId(dto.deviceId);
        vo.setUserAgent(dto.userAgent);
        vo.setBrowser(dto.browser);
        vo.setOs(dto.os);
        
        try {
        	pushDao.savePushSub( vo );
		} catch (Exception e) {
			log.error("", e);
			return new ReturnBasic("E", e.getMessage());
		}

    	return new ReturnBasic();
    }
    
    @Override
    public ReturnBasic deleteAllByUserIdAndEndpoint(LoginUser user, String endpoint) {
    	
        try {
			pushDao.removePushSub(endpoint);
		} catch (Exception e) {
			log.error("", e);
			return new ReturnBasic("E", e.getMessage());
		}
         
         return new ReturnBasic();
    }
    
    @Override
    public ReturnBasic sendPush(LoginUser user, PushNotiDataVo data) throws Exception {
    	
    	if( user == null || data == null ) {
    		return new ReturnBasic("E", "null 안됨.");
    	}
    	
    	final List<PushSubInfoVo> entityList = pushDao.findAllByUser(user);
    	if( AppUtils.isEmpty(entityList) ) {
    		return new ReturnBasic("E", "보낼 대상 없음.");
    	}
    	
    	data.getData().setContextPath("/" + appName);
    	
    	final String payload = objMapper.writeValueAsString(data);
    	final byte [] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
    	
    	int okCount = 0;
        int failCount = 0;
        
        final List<PushSendResVo> resList = new ArrayList<>();

        for (PushSubInfoVo sub : entityList) {
            final Notification notification = new Notification(
                sub.getEndPoint(), sub.getP256dh(), sub.getAuth(), payloadBytes
            );

            try {
                final HttpResponse res = pushService.send(notification);

                final int status = res.getStatusLine().getStatusCode();
                final String body = (res.getEntity() != null)
                    ? org.apache.http.util.EntityUtils.toString(res.getEntity(), StandardCharsets.UTF_8)
                    : null;

                if (status >= 200 && status < 300) {
                    okCount++;
                    resList.add( new PushSendResVo(sub, true) );
                } else {
                    failCount++;
                    log.warn("push fail status={} endpoint={} body={}", status, sub.getEndPoint(), body);

                    if (status == 404 || status == 410) {
                    	resList.add( new PushSendResVo(sub, null) );
                    } else {
                    	resList.add( new PushSendResVo(sub, false) );
                    }
                }
            } catch (Exception e) {
                failCount++;
                log.error("push send exception endpoint={} msg={}", sub.getEndPoint(), e.getMessage(), e);
                resList.add( new PushSendResVo(sub, false) );
            }
        }
        log.info("푸시 발송 결과 ok={} fail={}", okCount, failCount);
        
        final ReturnBasic result = new ReturnBasic();
        
        try {
			pushDao.updatePushSubRes(resList);
		} catch (Exception e) {
			log.error("", e);
			return result.setCodeMessage("E", e.getMessage());
		}

        if( okCount < 1 ) {
        	result.setCodeMessage("E", "푸시 발송 실패");
        }

    	return result;
    }
    
    @Override
    public ReturnBasic getPushStatus(LoginUser user) {
    	final ReturnBasic result = new ReturnBasic();
    	
    	result.setData( userDao.getPushStatus(user) );

    	return result;
    }
    
    @Override
    public ReturnBasic setPushStatus(LoginUser user, boolean enabled) {
    	
    	final ReturnBasic result = new ReturnBasic();
    	
    	try {
    		userDao.setPushStatus(user, enabled);
    		result.setData( Boolean.valueOf(enabled) );
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", e.getMessage());
		}
     
    	return result;
    }
    
    
}
