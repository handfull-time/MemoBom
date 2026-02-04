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
	public ShareDto loadShareInfo(LoginUser user, String uid, boolean isBot) {
		
    	final ShareVo vo = shareDao.loadShareInfo( user, uid, isBot );
    	
		final ShareDto result = new ShareDto(); 
		result.setTitle(appName);

		if( vo == null ) {
			result.setText("공유 주소를 확인하세요.");
    		return result;
    	}
		
		result.setText( vo.getText() );
		
		final EShareTargetType targetType = vo.getTargetType();
		
		final String fullUrl;
		
		if( targetType == EShareTargetType.Topic ) {
			fullUrl = "/Mosaic/index.html?uid=" + vo.getUid();
		}else if( targetType == EShareTargetType.Fragment ) {
			fullUrl = "/Fragment/index.html?fragUid=" + vo.getUid();
		}else {
			fullUrl = "";
		}
		
		result.setUrl(fullUrl);
		
		return result;
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
