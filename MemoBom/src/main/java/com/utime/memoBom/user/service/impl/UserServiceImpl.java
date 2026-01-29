package com.utime.memoBom.user.service.impl;

import org.springframework.stereotype.Service;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.service.UserService;
import com.utime.memoBom.user.vo.UserVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService{
	
	final UserDao userDao;

	@Override
	public UserVo getUserFromUid(String uid) {

		if( AppUtils.isEmpty(uid))
			return null;
		
		return userDao.getUserFromUid(uid);
	}

	@Override
	public ReturnBasic getMyWriteDataList(LoginUser user , String date) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( userDao.getMyWriteDataList(user, date) );
		
		return result;
	}
	
	
}
