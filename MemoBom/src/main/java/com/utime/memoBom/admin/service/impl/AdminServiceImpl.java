package com.utime.memoBom.admin.service.impl;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.utime.memoBom.admin.dao.AdminDao;
import com.utime.memoBom.admin.service.AdminService;
import com.utime.memoBom.admin.vo.AdminCommentVo;
import com.utime.memoBom.admin.vo.AdminFragmentVo;
import com.utime.memoBom.admin.vo.AdminSearchVo;
import com.utime.memoBom.admin.vo.AdminTopicVo;
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

	private static final String ERR_ADMIN_LOGIN_FORBIDDEN = "EADM001";
	private static final String ERR_TOPIC_NO_INVALID = "EADM002";
	private static final String ERR_FRAGMENT_NO_INVALID = "EADM003";
	private static final String ERR_FRAGMENT_NOT_FOUND = "EADM004";
	private static final String ERR_COMMENT_INPUT_INVALID = "EADM005";
	private static final String ERR_COMMENT_NOT_FOUND = "EADM006";
	private static final String ERR_COMMENT_UPDATE_EXCEPTION = "EADM007";
	private static final String ERR_USER_NO_REQUIRED = "EADM008";
	private static final String ERR_USER_NOT_FOUND_FOR_NOTE = "EADM009";
	private static final String ERR_USER_NOTE_UPDATE_EXCEPTION = "EADM010";
	private static final String ERR_USER_ENABLE_INPUT_INVALID = "EADM011";
	private static final String ERR_USER_NOT_FOUND_FOR_ENABLE = "EADM012";
	private static final String ERR_USER_ENABLE_UPDATE_EXCEPTION = "EADM013";
	private static final String ERR_TOPIC_NO_REQUIRED_FOR_UPDATE = "EADM014";
	private static final String ERR_TOPIC_NOT_FOUND_FOR_UPDATE = "EADM015";
	private static final String ERR_TOPIC_UPDATE_EXCEPTION = "EADM016";
	
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
	
	@Scheduled(cron = "0 30 12 * * *", zone = "Asia/Seoul")
	public void cleanupExpired() {
		try {
			final int pushDeleted = adminDao.deleteInactivePushSubsAfter2Weeks();
			final int commentDeleted = adminDao.deleteCommentsAfter2Weeks();
			final int fragmentDeleted = adminDao.deleteFragmentsAfter2Weeks();
			final int topicDeleted = adminDao.deleteDisabledTopicsAfter2Weeks();
			final int userDeleted = adminDao.deleteDisabledUsersAfter2Weeks();

			log.info(
				"cleanupExpired 완료 - push:{}, comments:{}, fragments:{}, topics:{}, users:{}",
				pushDeleted, commentDeleted, fragmentDeleted, topicDeleted, userDeleted
			);
		} catch (Exception e) {
			log.error("cleanupExpired 실행 중 오류", e);
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
			return result.setCodeMessage(ERR_ADMIN_LOGIN_FORBIDDEN, "접근 권한 불가.");
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
		
		result.setData( adminDao.getTopicList(this.procDate(searchVo)) );
		
		return result;
	}

	@Override
	public ReturnBasic getTopicFollowUsers(LoginUser user, Long topicNo) {
		final ReturnBasic result = new ReturnBasic();

		if (topicNo == null || topicNo <= 0) {
			return result.setCodeMessage(ERR_TOPIC_NO_INVALID, "유효한 topicNo 값이 필요합니다.");
		}

		result.setData(adminDao.getTopicFollowUsers(topicNo));
		return result;
	}

	@Override
	public ReturnBasic getFragmentList(LoginUser user, AdminSearchVo searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( adminDao.getFragmentList(this.procDate(searchVo)) );
		
		return result;
	}

	@Override
	public ReturnBasic getCommentList(LoginUser user, AdminSearchVo searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( adminDao.getCommentList(this.procDate(searchVo)) );
		
		return result;
	}

	@Override
	public ReturnBasic getFragmentPreview(LoginUser user, Long fragmentNo) {
		final ReturnBasic result = new ReturnBasic();

		if (fragmentNo == null || fragmentNo <= 0) {
			return result.setCodeMessage(ERR_FRAGMENT_NO_INVALID, "유효한 fragmentNo 값이 필요합니다.");
		}

		final AdminFragmentVo item = adminDao.getFragmentPreview(fragmentNo);
		if (item == null) {
			return result.setCodeMessage(ERR_FRAGMENT_NOT_FOUND, "프래그먼트를 찾지 못했습니다.");
		}

		result.setData(item);
		
		return result;
	}

	@Override
	public ReturnBasic updateCommentDeleted(LoginUser user, AdminCommentVo commentVo) {
		final ReturnBasic result = new ReturnBasic();

		if (commentVo == null || commentVo.getCommentNo() == null || commentVo.getDeleted() == null) {
			return result.setCodeMessage(ERR_COMMENT_INPUT_INVALID, "commentNo, deleted 값이 필요합니다.");
		}

		try {
			final int updated = adminDao.updateCommentDeleted(commentVo.getCommentNo(), commentVo.getDeleted());
			if (updated <= 0) {
				result.setCodeMessage(ERR_COMMENT_NOT_FOUND, "수정할 댓글을 찾지 못했습니다.");
			}
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage(ERR_COMMENT_UPDATE_EXCEPTION, e.getMessage());
		}
		return result;
	}

	@Override
	public ReturnBasic getEmotionList(LoginUser user, AdminSearchVo searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( adminDao.getEmotionList(this.procDate(searchVo)) );
		
		return result;
	}

	@Override
	public ReturnBasic getScrapList(LoginUser user, AdminSearchVo searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( adminDao.getScrapList(this.procDate(searchVo)) );
		
		return result;
	}

	@Override
	public ReturnBasic getShareList(LoginUser user, AdminSearchVo searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( adminDao.getShareList(this.procDate(searchVo)) );
		
		return result;
	}

	@Override
	public ReturnBasic getPushList(LoginUser user, AdminSearchVo searchVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( adminDao.getPushList(this.procDate(searchVo)) );
		
		return result;
	}

	@Override
	public ReturnBasic updateUserNote(LoginUser user, AdminUserVo userVo) {
		final ReturnBasic result = new ReturnBasic();

		if (userVo == null || userVo.getUserNo() == null) {
			return result.setCodeMessage(ERR_USER_NO_REQUIRED, "userNo 값이 필요합니다.");
		}

		try {
			final int updated = adminDao.updateUserNote(userVo.getUserNo(), userVo.getNote());
			if (updated <= 0) {
				result.setCodeMessage(ERR_USER_NOT_FOUND_FOR_NOTE, "저장할 사용자를 찾지 못했습니다.");
			}
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage(ERR_USER_NOTE_UPDATE_EXCEPTION, e.getMessage());
		}
		return result;
	}

	@Override
	public ReturnBasic updateUserEnabled(LoginUser user, AdminUserVo userVo) {
		final ReturnBasic result = new ReturnBasic();

		if (userVo == null || userVo.getUserNo() == null || userVo.getEnabled() == null) {
			return result.setCodeMessage(ERR_USER_ENABLE_INPUT_INVALID, "userNo, enabled 값이 필요합니다.");
		}

		try {
			final int updated = adminDao.updateUserEnabled(userVo.getUserNo(), userVo.getEnabled());
			if (updated <= 0) {
				result.setCodeMessage(ERR_USER_NOT_FOUND_FOR_ENABLE, "수정할 사용자를 찾지 못했습니다.");
			}
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage(ERR_USER_ENABLE_UPDATE_EXCEPTION, e.getMessage());
		}
		return result;
	}

	@Override
	public ReturnBasic updateTopicEditable(LoginUser user, AdminTopicVo topicVo) {
		final ReturnBasic result = new ReturnBasic();

		if (topicVo == null || topicVo.getTopicNo() == null) {
			return result.setCodeMessage(ERR_TOPIC_NO_REQUIRED_FOR_UPDATE, "topicNo 값이 필요합니다.");
		}

		try {
			int updated = adminDao.updateTopicEditable(topicVo);
			if (updated <= 0) {
				result.setCodeMessage(ERR_TOPIC_NOT_FOUND_FOR_UPDATE, "수정할 토픽을 찾지 못했습니다.");
			}
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage(ERR_TOPIC_UPDATE_EXCEPTION, e.getMessage());
		}
		return result;
	}

}
