package com.utime.memoBom.user.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.mapper.UserMapper;
import com.utime.memoBom.user.vo.MyWriterVo;
import com.utime.memoBom.user.vo.UserVo;
import com.utime.memoBom.user.vo.query.BasicUserVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class UserDaoImpl implements UserDao {
	
	private final UserMapper userMapper;
	
	@Override
	public UserVo findById(String provider, String id) {

		return userMapper.selectUserFromIdAndProvider( provider, id);
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
	
	@Override
	public UserVo getUserFromUid(String uid) {
		
		return userMapper.selectUserFromUid(uid);
	}
	
	@Override
	public List<MyWriterVo> getMyWriteDataList(LoginUser user, String date) {
		
		return userMapper.selectMyWriteDataList(user, date);
	}
	
	@Override
	public BasicUserVo getBasicUserFromUserNo(long userNo) {
		
		return userMapper.getBasicUserFromUserNo(userNo);
	}
}
