package com.utime.memoBom.board.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.utime.memoBom.board.dao.ShareDao;
import com.utime.memoBom.board.dto.ShareDto;
import com.utime.memoBom.board.service.ShareService;
import com.utime.memoBom.board.vo.EShareTargetType;
import com.utime.memoBom.board.vo.ShareVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.ReturnBasic;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class ShareServiceImpl implements ShareService{
	
	@Value("${appName}")
	private String appName;
	
	final ShareDao shareDao;
	
	@Override
	public ShareVo loadShareInfo(LoginUser user, String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnBasic makeShareInfo(HttpServletRequest request, LoginUser user, EShareTargetType targetType,
			String targetUid) {
		
		final ReturnBasic result = new ReturnBasic();
		
		ShareVo vo;
		try {
			vo = shareDao.addShareInfo(user, targetType, targetUid);
		} catch (Exception e) {
			log.error("", e);
			return result.setCodeMessage("E", "공유 대상 조회에 실패했습니다.");
		}
		
		if( vo == null ) {
			return result.setCodeMessage("E", "공유 대상 정보가 없습니다.");
		}
		
		final ShareDto shareDto = new ShareDto();
		
		shareDto.setTitle(appName);
		shareDto.setText(vo.getText());
		
		final String fullUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath() + "/Share/" + vo.getUid() + ".html";
		shareDto.setUrl(fullUrl);
		
		result.setData(shareDto);
		
		return result;
	}

}
