package com.utime.memoBom.board.service.impl;

import java.util.UUID;

import org.springframework.http.HttpHeaders;

import com.utime.memoBom.common.dao.KeyValueDao;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.EDevicePlatform;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class KeyUtil {
	
	/**
	 * 키를 새로 만들기
	 * @param request
	 * @param user
	 * @return
	 */
	public static String createKey(KeyValueDao keyValueDao, HttpServletRequest request, UserVo user) {
		
		final UUID guid = UUID.randomUUID();
		final String result = guid.toString();
		
		final String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
		
		final UserDevice device = AppUtils.getDeviceInfoFromUserAgent(userAgent);
		final EDevicePlatform dp = device.getDevice();
		if( dp == null || dp == EDevicePlatform.Unknown ) {
			log.info("알 수 없는 장치: {}", userAgent);
			return null;
		}
		
		final String addValue = user.getUid() + device.getModel() + dp;
		
		keyValueDao.setValue(result, addValue, 50);
		
		return result;
	}
	
	/**
	 * 만들어진 키 검증
	 * @param request
	 * @param user
	 * @param seal
	 * @return
	 */
	public static ReturnBasic checkKey(KeyValueDao keyValueDao, UserVo user, UserDevice device, String seal) {
		
		final String sealValue = keyValueDao.getValueAndRemove(seal);
		
		if( AppUtils.isEmpty(sealValue)) {
			return new ReturnBasic("E", "유효시간이 초과 되거나 잘못된 요청 입니다.");
		}
		
		final EDevicePlatform dp = device.getDevice();
		if( dp == null || dp == EDevicePlatform.Unknown ) {
			log.info("알 수 없는 장치");
			return new ReturnBasic("E", "잘못된 장치 입니다.");
		}
		
		final String compareValue = user.getUid() + device.getModel() + dp;
		if( !sealValue.equals(compareValue) ) {
			log.warn("접속 정보 오류 {} - {}", sealValue, compareValue);
			return new ReturnBasic("E", "잘못된 요청 입니다.");
		}
		
		return new ReturnBasic();
	}
}
