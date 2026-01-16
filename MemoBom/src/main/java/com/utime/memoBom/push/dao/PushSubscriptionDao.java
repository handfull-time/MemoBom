package com.utime.memoBom.push.dao;

import java.util.List;

import com.utime.memoBom.push.vo.PushSubscriptionEntity;
import com.utime.memoBom.user.vo.UserVo;

public interface PushSubscriptionDao {

	/**
	 * 구독 정보 조회
	 * @param endpoint
	 * @return
	 */
	PushSubscriptionEntity findByEndpoint(String endpoint);

	/**
	 * 구독 정보 추가.
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	int save(PushSubscriptionEntity entity) throws Exception;

	/**
	 * 구독 정보 제거
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	int removeSubscription(PushSubscriptionEntity entity)throws Exception;

	/**
	 * 사용자 구독 정보 조회
	 * @param user
	 * @return
	 */
	List<PushSubscriptionEntity> findAllByUser(UserVo user);

	

}
