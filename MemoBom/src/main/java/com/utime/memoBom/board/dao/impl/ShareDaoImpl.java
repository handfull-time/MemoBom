package com.utime.memoBom.board.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.memoBom.board.dao.ShareDao;
import com.utime.memoBom.board.mapper.ShareMapper;
import com.utime.memoBom.board.vo.EShareTargetType;
import com.utime.memoBom.board.vo.ShareVo;
import com.utime.memoBom.board.vo.query.ShareDataVo;
import com.utime.memoBom.board.vo.query.ShareTargetInfo;
import com.utime.memoBom.common.security.LoginUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class ShareDaoImpl implements ShareDao{
	final ShareMapper shareMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ShareVo addShareInfo(LoginUser user, EShareTargetType targetType, String uid) throws Exception {
		
		ShareTargetInfo targetInfo;
		if( targetType == EShareTargetType.Topic ) {
			targetInfo = shareMapper.loadTopicInfo(uid);
		}else if( targetType == EShareTargetType.Fragment ) {
			targetInfo = shareMapper.loadFragmentInfo(uid);
		}else {
			return null;
		}
		
		if( targetInfo == null ) {
			return null;
		}
		
		String txt = targetInfo.getText();
		if( txt.length() > 64 ) {
			txt = txt.substring(0, 60) + "...";
		}
		
		final ShareDataVo data = new ShareDataVo();
		data.setTargetType(targetType);
		data.setTargetNo(targetInfo.getTargetNo());
		data.setUserNo(user==null? 0:user.userNo());

		shareMapper.insertShareData(data);
		final ShareDataVo dbData = shareMapper.selectShareData(data.getShareNo(), null);

		final ShareVo share = new ShareVo();
		share.setText( txt );
		share.setUid( dbData.getUid() );

		return share;
	}
}
