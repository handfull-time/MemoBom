package com.utime.memoBom.push.service;

import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.push.dto.PushSubscriptionDto;
import com.utime.memoBom.user.vo.UserVo;

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
	ReturnBasic upsert(UserVo user, PushSubscriptionDto dto);

	/**
	 * 구독 해제
	 * @param user
	 * @param endpoint
	 * @return
	 */
	ReturnBasic deleteAllByUserIdAndEndpoint(UserVo user, String endpoint);
	
	/**
	 * 푸시 발송
	 * @param user
	 * @param obj
	 * @return
	 */
	ReturnBasic sendPush(UserVo user, Object obj) throws Exception;

}
