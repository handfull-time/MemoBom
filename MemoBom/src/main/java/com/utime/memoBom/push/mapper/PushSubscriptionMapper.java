package com.utime.memoBom.push.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.utime.memoBom.push.vo.PushSubVo;
import com.utime.memoBom.push.vo.query.PushSubInfoVo;

/**
 * 푸시 관련 Mapper
 */
@Mapper
public interface PushSubscriptionMapper {
	
	/**
	 * 구독 정보 추가
	 * @param vo
	 * @return
	 */
	int insertPushSub(PushSubVo vo );
	
	/**
	 * 구독 정보 수정
	 * @param vo
	 * @return
	 */
	int updatePushSub(PushSubVo vo );
	
	/**
	 * 회원 푸시 정보 조회
	 * @param userNo
	 * @return
	 */
	List<PushSubInfoVo> selectActivePushSubsByUser( long userNo );
	
	/**
	 * 푸시 정보 조회
	 * @param endPoint
	 * @return
	 */
	PushSubInfoVo selectPushSubByEndpoint( String endPoint );
	
	/**
	 * 구독 정보 제거
	 * @param endPoint
	 * @return
	 */
	int removeSubscription( String endPoint );

	/**
	 * 발송 성공
	 * @param subNo
	 * @return
	 */
	int markSuccess(long subNo);

	/**
	 * 발송 실패
	 * @param subNo
	 * @return
	 */
	int markFail(long subNo);

	/**
	 * 구독 정보 비활성화.
	 * @param subNo
	 * @return
	 */
	int markInactive(long subNo);
}