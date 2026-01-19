package com.utime.memoBom.admin.dao.impl;

import org.springframework.stereotype.Repository;

import com.utime.memoBom.admin.dao.AdminDao;
import com.utime.memoBom.admin.mapper.AdminMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class AdminDaoImpl implements AdminDao {
	
	private AdminMapper adminMapper;
}
