package com.utime.memoBom.push.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.push.dao.PushSubscriptionDao;
import com.utime.memoBom.push.dto.PushSubscriptionDto;
import com.utime.memoBom.push.service.PushSendService;
import com.utime.memoBom.push.vo.PushSubscriptionEntity;
import com.utime.memoBom.user.vo.UserVo;

import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

@Slf4j
@Service
class PushSendServiceImpl implements PushSendService {

	private final PushSubscriptionDao repo;
    private final PushService pushService;
    
    @Autowired
    private ObjectMapper objMapper;
    
    public PushSendServiceImpl(
    		PushSubscriptionDao repo,
            @Value("${app.push.vapid.public-key}") String publicKey,
            @Value("${app.push.vapid.private-key}") String privateKey,
            @Value("${app.push.vapid.subject}") String subject) throws Exception {

        this.repo = repo;

        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        this.pushService = new PushService(publicKey, privateKey, subject);
    }

    @Override
    public ReturnBasic upsert(UserVo user, PushSubscriptionDto dto) {
        
        PushSubscriptionEntity entity = repo.findByEndpoint(dto.endpoint);
        
        if( entity == null ) {
        	entity = new PushSubscriptionEntity();
        }

        entity.setUserNo(user.getUserNo());
        entity.setEndPoint(dto.endpoint);
        entity.setP256dh(dto.keys.p256dh);
        entity.setAuth(dto.keys.auth);

        try {
			repo.save(entity);
		} catch (Exception e) {
			log.error("", e);
			return new ReturnBasic("", e.getMessage());
		}

    	return new ReturnBasic();
    }
    
    @Override
    public ReturnBasic deleteAllByUserIdAndEndpoint(UserVo user, String endpoint) {
    	
    	 final PushSubscriptionEntity entity = repo.findByEndpoint(endpoint);
         
         if( entity != null ) {
             try {
				repo.removeSubscription(entity);
			} catch (Exception e) {
				log.error("", e);
				return new ReturnBasic("", e.getMessage());
			}
         }
         
         return new ReturnBasic();
    }
    
    @Override
    public ReturnBasic sendPush(UserVo user, Object obj) throws Exception {
    	
    	if( user == null || obj == null ) {
    		return new ReturnBasic("E", "null 안됨.");
    	}
    	
    	final List<PushSubscriptionEntity> entityList = repo.findAllByUser(user);
    	if( AppUtils.isEmpty(entityList) ) {
    		return new ReturnBasic("E", "보낼 대상 없음.");
    	}
    	
    	final String payload = objMapper.writeValueAsString(obj);
    	final byte [] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
    	
    	int sendCount = 0;
    	for( PushSubscriptionEntity entity : entityList ) {
    		final Notification notification;
	        try {
				notification = new Notification(
				        entity.getEndPoint(),
				        entity.getP256dh(),
				        entity.getAuth(),
				        payloadBytes
				);
			} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
				log.error("", e);
				throw e;
			}

	        try {
	            pushService.send(notification);
	            sendCount++;
	        } catch (Exception e) {
	        	log.error("push send fail.", e.getMessage());
	            repo.removeSubscription(entity);
	        }
    	}
    	
    	log.info("푸시 발송 건수 : {}", sendCount );

    	return new ReturnBasic();
    }
}
