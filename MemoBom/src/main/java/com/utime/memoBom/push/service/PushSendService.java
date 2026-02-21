package com.utime.memoBom.push.service;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.push.dto.PushSubscriptionDto;
import com.utime.memoBom.push.vo.PushSendDataVo;

/**
 * 푸시 서비스
 */
public interface PushSendService {

	/**
	 * 구독 신청
	 * @param user
	 * @param dto
	 * @return
	 */
	ReturnBasic upsert(LoginUser user, PushSubscriptionDto dto);

	/**
	 * 구독 해제
	 * @param user
	 * @param endpoint
	 * @return
	 */
	ReturnBasic deleteAllByUserIdAndEndpoint(LoginUser user, String endpoint);
	
	/**
	 * 푸시 발송
	 * @param user
	 * @param obj
	 * @return
	 */
	ReturnBasic sendPush(LoginUser user, PushSendDataVo data) throws Exception;

	/**
	 * 푸시 수신 상태
	 * @param user
	 * @return
	 */
	ReturnBasic getPushStatus(LoginUser user, String deviceId);

	/**
	 * 푸시 수신 설정
	 * @param user
	 * @param enabled true:수신, false:미수신
	 * @return
	 */
	ReturnBasic setPushStatus(LoginUser user, boolean enabled);

	/**
	 * 푸시 메시지 클릭 처리
	 * @param clickId
	 * @return
	 */
	ReturnBasic procClickEvent(String clickId);

	/**
	 * 새로운 fragment 생성 알림
	 * @param user
	 * @param topicUid
	 * @return
	 */
	int sendMessageNewFragment(LoginUser user, String topicUid);

}
