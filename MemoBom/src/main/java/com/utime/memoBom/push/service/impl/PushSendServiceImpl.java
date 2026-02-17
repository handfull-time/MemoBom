package com.utime.memoBom.push.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utime.memoBom.admin.dao.AdminAlarmDao;
import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.push.dao.PushSubscriptionDao;
import com.utime.memoBom.push.dto.PushNotiDataDto;
import com.utime.memoBom.push.dto.PushSubscriptionDto;
import com.utime.memoBom.push.service.PushSendService;
import com.utime.memoBom.push.vo.PushSendDataVo;
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
    private final AdminAlarmDao alarmDao;
    private final UserDao userDao;
    private final TopicDao topicDao;
    
    @Value("${appName}")
	private String appName;
    
    @Autowired
    private ObjectMapper objMapper;
    
    public PushSendServiceImpl(
    		PushSubscriptionDao repo,
    		UserDao userDao,
    		AdminAlarmDao alarmDao,
    		TopicDao topicDao,
            @Value("${app.push.vapid.public-key}") String publicKey,
            @Value("${app.push.vapid.private-key}") String privateKey,
            @Value("${app.push.vapid.subject}") String subject) throws Exception {

        this.pushDao = repo;
        this.userDao = userDao;
        this.alarmDao = alarmDao;
        this.topicDao = topicDao;

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
    public ReturnBasic sendPush(LoginUser user, PushSendDataVo data) throws Exception {
    	
    	if( user == null || data == null ) {
    		return new ReturnBasic("E", "null 안됨.");
    	}
    	
    	final List<PushSubInfoVo> entityList = pushDao.findPushSubsByUser(user);
    	if( AppUtils.isEmpty(entityList) ) {
    		return new ReturnBasic("E", "보낼 대상 없음.");
    	}
    	
		final PushNotiDataDto pushDto = new PushNotiDataDto();
		pushDto.setTitle(data.getTitle());
		pushDto.setMessage(data.getMessage());
		pushDto.setIcon(data.getImageUrl());
		
    	final UUID uid = UUID.randomUUID();
    	
		final PushNotiDataDto._Data dtoData = pushDto.getData();
		dtoData.setContextPath("/" + appName);
		dtoData.setClickId( uid );
		dtoData.setUrl(data.getLinkUrl());
    	
    	final String payload = objMapper.writeValueAsString(pushDto);
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
			alarmDao.addPushAlarm(user, data, uid);
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
    
    
    @Override
    public ReturnBasic procClickEvent(String clickId) {
    	
    	try {
    		alarmDao.readPushAlarm( clickId );
		} catch (Exception e) {
			log.error("", e);
		}
    	
    	return new ReturnBasic();
    }
    
    @Override
    public int sendMessageNewFragment(LoginUser user, String topicUid) {
    	
        final ExecutorService executor = Executors.newFixedThreadPool(1);

    	final TopicVo vo = topicDao.loadTopic(topicUid);
    	
        executor.submit(() -> {
        	final List<LoginUser> userList = topicDao.getTopicFollowList( user, topicUid );
        	if( userList.isEmpty() ) {
        		log.info("팔로우 사용자가 없습니다.");
        		return;
        	}
        	
        	final PushSendDataVo data = new PushSendDataVo();
        	data.setTitle(appName + " 새글 알림");
        	data.setMessage( vo.getName() + " 새글이 올라왔습니다." );
        	data.setImageUrl("/"+appName+"/images/fragment/fragmentWriteButton.svg");
        	data.setLinkUrl("/Fragment/index.html?topicUid=" + topicUid);
        	
        	userList.forEach( item -> {
            	log.info("{} topic. UserNo : {}. send push - {}", vo.getName(), item.userNo(),  data.toString());
            	
        		try {
					sendPush( item, data);
				} catch (Exception e) {
					log.error("", e);
				}
        	});
        	
        });

        log.info("{} topic. complete", vo.getName());
        
        executor.shutdown();
    	
    	return 1;
    }
    
}
