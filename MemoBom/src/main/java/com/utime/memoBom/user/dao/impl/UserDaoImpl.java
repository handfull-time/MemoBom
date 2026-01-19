package com.utime.memoBom.user.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.mapper.UserMapper;
import com.utime.memoBom.user.vo.UserVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class UserDaoImpl implements UserDao {
	
	private final UserMapper userMapper;
	
	@Override
	public UserVo findByEmail(String provider, String email) {

		return userMapper.selectUserFromIdAndProvider( provider, email);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int addUser(UserVo user) throws Exception {
		
		return userMapper.insertUser( user );
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int addLoginRecord(UserVo result, String ip, UserDevice device) throws Exception {
		
		return userMapper.insertLoginRecord(result, ip, device);
	}
	
	@Override
	public int deleteUser(UserVo userVo) throws Exception {
		
		return userMapper.removeUser(userVo);
	}
}
