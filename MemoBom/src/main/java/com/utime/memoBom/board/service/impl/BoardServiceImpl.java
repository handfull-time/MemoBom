package com.utime.memoBom.board.service.impl;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.utime.memoBom.board.dao.BoardDao;
import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.service.BoardService;
import com.utime.memoBom.board.vo.BoardReqVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.dao.KeyValueDao;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.EDevicePlatform;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class BoardServiceImpl implements BoardService {

	final BoardDao boardDao;
	
	final TopicDao topicDao;
	
	final KeyValueDao keyValueDao;

	@Override
	public String createKey(HttpServletRequest request) {
		
		final UUID guid = UUID.randomUUID();
		final String result = guid.toString();
		
		final String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
		
		final UserDevice device = AppUtils.getDeviceInfoFromUserAgent(userAgent);
		final EDevicePlatform dp = device.getDevice();
		if( dp == null || dp == EDevicePlatform.Unknown ) {
			log.info("알 수 없는 장치: {}", userAgent);
			return null;
		}
		
		final String addValue = device.getModel() + dp;
		
		keyValueDao.setValue(result, addValue, 50);
		
		return result;
	}
	
	@Override
	public Object getTopicBoardListFromTopicUid(UserVo user, String topicUid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getBoardList(UserVo user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getTopicBoardListFromUserUid(UserVo user, String userUid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnBasic saveFragment(UserVo user, UserDevice device, BoardReqVo reqVo) {
		
		final String sealValue = keyValueDao.getValueAndRemove(reqVo.getSeal());
		
		if( AppUtils.isEmpty(sealValue)) {
			return new ReturnBasic("E", "유효시간이 초과 되거나 잘못된 요청 입니다.");
		}
		
		final EDevicePlatform dp = device.getDevice();
		final String compareValue = device.getModel() + dp;
		if( !sealValue.equals(compareValue) ) {
			log.warn("접속 정보 오류 {} - {}", sealValue, compareValue);
			return new ReturnBasic("E", "잘못된 요청 입니다.");
		}
		
		// 오른쪽 공백 제거
		reqVo.setContent( reqVo.getContent().stripTrailing() );
		
		if( AppUtils.isEmpty(reqVo.getContent())) {
			log.warn("냉무 ...");
			return new ReturnBasic("E", "잘못된 요청 입니다.");
		}
		
		final ReturnBasic result = new ReturnBasic();
		
		try {
			boardDao.saveFragment(user, device, reqVo);
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "An error occurred while saving.");
		}
		
		return result;
	}

}
