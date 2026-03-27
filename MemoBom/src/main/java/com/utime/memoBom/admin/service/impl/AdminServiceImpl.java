package com.utime.memoBom.admin.service.impl;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.utime.memoBom.admin.dao.AdminDao;
import com.utime.memoBom.admin.service.AdminService;
import com.utime.memoBom.admin.vo.AdminSearchVo;
import com.utime.memoBom.admin.vo.AdminUserVo;
import com.utime.memoBom.admin.vo.DashboardVo;
import com.utime.memoBom.common.security.JwtProvider;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.EJwtRole;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class AdminServiceImpl implements AdminService {
	
	final AdminDao adminDao;
	
	final UserDao userDao;
	
	final JwtProvider provider;
	
	@Value("${appName}")
	private String appName;

	@Value("${env.admin.userNo}")
	private String adminUserNo;
	
	private Set<Long> adminUserNoSet;
	
	private final String masterId = "BomMaster";
	
	private final String masterProvider = "localhost";
	
	@PostConstruct
	private void init() throws Exception{

		this.adminUserNoSet = Arrays.stream(adminUserNo.split(","))
		        .map(String::trim)
		        .filter(s -> s.matches("\\d+"))
		        .map(Long::valueOf)
		        .collect(Collectors.toSet());
		
		UserVo admin = userDao.findById(masterProvider, masterId);
		if( admin == null ) {
			admin = new UserVo();
    		admin.setId(masterId);
    		admin.setProvider(masterProvider);
    		admin.setEnabled(true);
    		admin.setEmail(masterId );
    		admin.setRole(EJwtRole.Admin);
    		admin.setNickname( "관리자" );
    		admin.setProfileUrl("/" +  appName + "/images/profile-placeholder.svg" );
    		
    		log.info("관리자 추가 정보 : {}", admin);
    		
    		userDao.addUser(admin);
		}
	}
	
	/**
	 * 어드민 계정 이전 계정 정보
	 */
	private final String BeforUserUid = "userUid";
	
	@Override
	public ReturnBasic adminLogin(HttpServletRequest request, HttpServletResponse response, LoginUser user) throws Exception {
		
		final ReturnBasic result = new ReturnBasic();
		
		if( ! this.adminUserNoSet.contains(user.userNo()) ) {
			return result.setCodeMessage("E", "접근 권한 불가.");
		}
		
		provider.deleteCookie(request, response, BeforUserUid);
		provider.addCookie(request, response, BeforUserUid, user.uid(), -1L);
		
		final UserVo admin = userDao.findById(masterProvider, masterId);
		
		provider.procLogin(request, response, admin);
		
		return result;
	}
	
	@Override
	public ReturnBasic adminLogout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		final ReturnBasic result = new ReturnBasic();
		
		final String userUid = provider.getCookieValue(request, BeforUserUid);
		if( AppUtils.isNotEmpty(userUid) ) {
			provider.deleteCookie(request, response, BeforUserUid);
			
			final UserVo user = userDao.getUserFromUid(userUid);
			
			provider.procLogin(request, response, user);
		}else {
			provider.procLogout(request, response);
		}
		
		return result;
	}
	
    private final java.time.format.DateTimeFormatter formatter =
            java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
	/**
	 * date가 null/blank 이거나 yyyy-MM-dd 형식이 아니면 오늘 날짜 반환
	 *
	 * <p>설계 의도</p>
	 * <ul>
	 *   <li>정규식으로 1차 필터링 후 {@link java.time.LocalDate} 파싱으로 2차 검증</li>
	 *   <li>유효성 실패 시 단일 fallback (오늘 날짜)</li>
	 *   <li>Thread-safe (java.time 사용)</li>
	 * </ul>
	 *
	 * @param date 입력 문자열 (yyyy-MM-dd 기대)
	 * @return 유효하면 그대로 반환, 아니면 오늘 날짜 (yyyy-MM-dd)
	 */
	private String getDate(String date) {

	    if (date == null || date.isBlank()) {
	        return java.time.LocalDate.now().format(formatter);
	    }

	    // 1차 형식 체크
	    if (!date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
	        return java.time.LocalDate.now().format(formatter);
	    }

	    // 2차 실제 날짜 검증 (예: 2026-02-30 방지)
	    try {
	        java.time.LocalDate.parse(date, formatter);
	        return date;
	    } catch (java.time.format.DateTimeParseException e) {
	        return java.time.LocalDate.now().format(formatter);
	    }
	}
	
	
	private AdminSearchVo procDate(AdminSearchVo searchVo) {
		searchVo.setDate( this.getDate(searchVo.getDate()) );
		return searchVo;
	}
	
	@Override
	public DashboardVo getDashboard(String date) {
		
		
		return adminDao.getDashboard( this.getDate(date) );
	}

	@Override
	public ReturnBasic getUserList(LoginUser user, AdminSearchVo searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( adminDao.getUserList(this.procDate(searchVo)) );
		
		return result;
	}

	@Override
	public ReturnBasic getTopicList(LoginUser user, AdminSearchVo searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( adminDao.getTopicList(searchVo) );
		
		return result;
	}

	@Override
	public ReturnBasic getFragmentList(LoginUser user, AdminSearchVo searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( adminDao.getFragmentList(searchVo) );
		
		return result;
	}

	@Override
	public ReturnBasic getCommentList(LoginUser user, AdminSearchVo searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( adminDao.getCommentList(searchVo) );
		
		return result;
	}

	@Override
	public ReturnBasic getEmotionList(LoginUser user, AdminSearchVo searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( adminDao.getEmotionList(searchVo) );
		
		return result;
	}

	@Override
	public ReturnBasic getScrapList(LoginUser user, AdminSearchVo searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( adminDao.getScrapList(searchVo) );
		
		return result;
	}

	@Override
	public ReturnBasic getShareList(LoginUser user, AdminSearchVo searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( adminDao.getShareList(searchVo) );
		
		return result;
	}

	@Override
	public ReturnBasic getPushList(LoginUser user, AdminSearchVo searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( adminDao.getPushList(searchVo) );
		
		return result;
	}

	@Override
	public ReturnBasic updateUserNote(LoginUser user, AdminUserVo userVo) {
		final ReturnBasic result = new ReturnBasic();

		if (userVo == null || userVo.getUserNo() == null) {
			return result.setCodeMessage("E", "userNo 값이 필요합니다.");
		}

		final int updated = adminDao.updateUserNote(userVo.getUserNo(), userVo.getNote());
		if (updated <= 0) {
			return result.setCodeMessage("E", "저장할 사용자를 찾지 못했습니다.");
		}
		return result;
	}

	@Override
	public ReturnBasic updateUserEnabled(LoginUser user, AdminUserVo userVo) {
		final ReturnBasic result = new ReturnBasic();

		if (userVo == null || userVo.getUserNo() == null || userVo.getEnabled() == null) {
			return result.setCodeMessage("E", "userNo, enabled 값이 필요합니다.");
		}

		final int updated = adminDao.updateUserEnabled(userVo.getUserNo(), userVo.getEnabled());
		if (updated <= 0) {
			return result.setCodeMessage("E", "수정할 사용자를 찾지 못했습니다.");
		}
		return result;
	}

}
