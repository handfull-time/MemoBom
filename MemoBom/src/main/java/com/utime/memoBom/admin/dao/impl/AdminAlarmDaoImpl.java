package com.utime.memoBom.admin.dao.impl;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.memoBom.admin.dao.AdminAlarmDao;
import com.utime.memoBom.admin.mapper.AdminAlarmMapper;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.push.vo.PushSendDataVo;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class AdminAlarmDaoImpl implements AdminAlarmDao {
	
	private final AdminAlarmMapper alarmMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int addPushAlarm(LoginUser user, PushSendDataVo data, UUID uid) throws Exception {

		return alarmMapper.insertPushAlarm(user, data, uid);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int readPushAlarm(String uid) throws Exception {
		
		return alarmMapper.updateAlarmCheck(uid);
	}
	
}
