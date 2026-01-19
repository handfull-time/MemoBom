package com.utime.memoBom.push.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.push.vo.PushSubscriptionEntity;
import com.utime.memoBom.user.vo.UserVo;

/**
 * 최초 필수 테이블 관련 Mapper
 */
@Mapper
public interface PushSubscriptionMapper {
	
	/**
	 * endpont 매칭 조회
	 * @param endpoint
	 * @return
	 */
	PushSubscriptionEntity findByEndpoint(@Param("endpoint") String endpoint);

	/**
	 * 구독 정보 추가
	 * @param entity
	 * @return
	 */
	int insertSubscription(PushSubscriptionEntity entity);

	/**
	 * 구독 정보 수정
	 * @param entity
	 * @return
	 */
	int updateSubscription(PushSubscriptionEntity entity);

	/**
	 * 비활성 정보 제거
	 * @param entity
	 * @return
	 */
	int removeSubscription(PushSubscriptionEntity entity);

	/**
	 * 사용자 구독 정보 조회
	 * @param user
	 * @return
	 */
	List<PushSubscriptionEntity> findAllByUser(UserVo user);
	
	

}