package com.utime.memoBom.user.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.memoBom.common.mapper.CommonMapper;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.mapper.UserMapper;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class UserDaoImpl implements UserDao {
	
//	private final String AdminId = "Admin";
	
	private final CommonMapper common;
	
	private final UserMapper userMapper;
	
//	@Value("${security.pwSaltKey}")
//    private String saltKey;
	
	@PostConstruct
	private void init() throws Exception{
		int result = 0;
		if( ! common.existTable("MB_USER") ) {
			log.info("MB_USER 생성");
			result += userMapper.createUser();
		}
		
		if( ! common.existTable("MB_USER_LOGIN_RECORD") ) {
			log.info("MB_USER_LOGIN_RECORD 생성");
			result += userMapper.createLoginRecord();
			result += common.createIndex("MB_USER_LOGIN_RECORD_IP_INDX", "MB_USER_LOGIN_RECORD", "IP");
			result += common.createIndex("MB_USER_LOGIN_RECORD_USER_NO_INDX", "MB_USER_LOGIN_RECORD", "USER_NO");
			result += common.createIndex("MB_USER_LOGIN_RECORD_REG_DATE_INDX", "MB_USER_LOGIN_RECORD", "REG_DATE");
		}

		log.info("초기 작업 {}", result);
	}
	
//	/**
//	 * 회원 비번 생성
//	 * @param user
//	 * @param pw
//	 * @return
//	 */
//	private String genPwString( UserVo user, String pw ) {
//		return saltKey + "[" + user.getId() + "]-{" +  user.getUserNo() + "}" + pw;
//	}

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
