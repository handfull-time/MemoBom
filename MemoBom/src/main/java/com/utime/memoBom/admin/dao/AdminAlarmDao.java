package com.utime.memoBom.admin.dao;

import java.util.UUID;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.push.vo.PushSendDataVo;

public interface AdminAlarmDao {

	/**
	 * 푸시 내용 알람 추가
	 * @param user
	 * @param data
	 * @return
	 * @throws Exception
	 */
	int addPushAlarm( LoginUser user, PushSendDataVo data, UUID uid ) throws Exception;

	/**
	 * 푸시로 알람 읽음 처리.
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	int readPushAlarm(String uid) throws Exception;
}
