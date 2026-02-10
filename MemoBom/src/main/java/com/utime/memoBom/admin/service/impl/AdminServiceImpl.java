package com.utime.memoBom.admin.service.impl;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.utime.memoBom.admin.dao.AdminDao;
import com.utime.memoBom.admin.service.AdminService;
import com.utime.memoBom.common.security.JwtProvider;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.EJwtRole;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class AdminServiceImpl implements AdminService {
	
	final AdminDao adminDao;
	
	final UserDao userDao;
	
	final JwtProvider provider;
	

	@Value("${env.admin.userNo}")
	private String adminUserNo;
	
	private Set<Long> adminUserNoSet;
	
	@PostConstruct
	private void init() {

		this.adminUserNoSet = Arrays.stream(adminUserNo.split(","))
		        .map(String::trim)
		        .filter(s -> s.matches("\\d+"))
		        .map(Long::valueOf)
		        .collect(Collectors.toSet());
	}
	
	@Override
	public ReturnBasic adminLogin(HttpServletRequest request, HttpServletResponse response, LoginUser user) throws Exception {
		final ReturnBasic result = new ReturnBasic();
		
		if( ! this.adminUserNoSet.contains(user.userNo()) ) {
			return result.setCodeMessage("E", "접근 권한 불가.");
		}
		
		final UserVo userDb = userDao.getUserFromUid(user.uid());
		if( userDb == null ) {
			return result.setCodeMessage("E", "접근 계정 불가.");
		}
		
		userDb.setRole(EJwtRole.Admin);
		provider.procLogin(request, response, userDb);
		
		return result;
	}
}
