package com.utime.memoBom.push.dao;

import java.util.List;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.push.vo.PushSendResVo;
import com.utime.memoBom.push.vo.PushSubVo;
import com.utime.memoBom.push.vo.query.PushSubInfoVo;

public interface PushSubscriptionDao {

	/**
	 * 구독 정보 저장
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	int savePushSub(PushSubVo vo) throws Exception;

	/**
	 * 구독 정보 제거
	 * @param endpoint
	 * @return
	 * @throws Exception
	 */
	int removePushSub(String endpoint) throws Exception;

	/**
	 * 사용자 구독 정보 조회
	 * @param user
	 * @return
	 */
	List<PushSubInfoVo> findPushSubsByUser(LoginUser user);

	/**
	 * 푸시 발송 결과 업데이트
	 * @param resList
	 * @return
	 * @throws Exception
	 */
	int updatePushSubRes(List<PushSendResVo> resList)throws Exception;

	

}
