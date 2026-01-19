package com.utime.memoBom.admin.service.impl;

import org.springframework.stereotype.Service;

import com.utime.memoBom.admin.dao.AdminDao;
import com.utime.memoBom.admin.service.AdminService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class AdminServiceImpl implements AdminService {
	
	final AdminDao adminDao;
}
