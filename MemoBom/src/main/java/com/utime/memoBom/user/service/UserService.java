package com.utime.memoBom.user.service;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.BinResultVo;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.dto.MyPageDto;
import com.utime.memoBom.user.dto.MySearchDto;
import com.utime.memoBom.user.dto.UserUpdateDto;
import com.utime.memoBom.user.vo.UserVo;

public interface UserService {

	/**
	 * 사용자 정보 조회
	 * @param uid
	 * @return
	 */
	UserVo getUserFromUid(String uid);
	
	/**
	 * 내 개인 정보 및 통계
	 * @param user
	 * @return
	 */
	MyPageDto getMyPage(LoginUser user);

	/**
	 * 내 작성 데이터
	 * @param user
	 * @param date yyyyMM
	 * @return
	 */
	ReturnBasic getMyCalendarDataList(LoginUser user, String date);

	/**
	 * 알람 목록
	 * @param user
	 * @param searchVo
	 * @return
	 */
	ReturnBasic getMyAlarmDataList(LoginUser user, MySearchDto searchVo);

	/**
	 * 이야기  목록
	 * @param user
	 * @param searchVo
	 * @return
	 */
	ReturnBasic getMyFragmentsDataList(LoginUser user, MySearchDto searchVo);

	/**
	 * 얘깃거리 목록
	 * @param user
	 * @param searchVo
	 * @return
	 */
	ReturnBasic getMyTopicDataList(LoginUser user, MySearchDto searchVo);

	/**
	 * 뎃글 목록
	 * @param user
	 * @param searchVo
	 * @return
	 */
	ReturnBasic getMyCommentsDataList(LoginUser user, MySearchDto searchVo);

	/**
	 * 개인 정보 수정
	 * @param user
	 * @param data
	 * @return
	 */
	ReturnBasic updateMyInfo(LoginUser user, UserUpdateDto data);

	/**
	 * 글자 크기 변경
	 * @param user
	 * @param data
	 * @return
	 */
	ReturnBasic updateMyInfoFontSize(LoginUser user, UserUpdateDto data);

	/**
	 * 회원의 email이 일치하는지 검사.
	 * @param user
	 * @param email
	 * @return
	 */
	ReturnBasic checkUser(LoginUser user, String email);
	
	/**
	 * 사용자 이미지 정보
	 * @param uid
	 * @return
	 */
	BinResultVo getUserProfile(String uid);

	/**
	 * 회원이 보관함 한 글 목록
	 * @param user
	 * @param searchVo
	 * @return
	 */
	ReturnBasic getMyScrapDataList(LoginUser user, MySearchDto searchVo);

	/**
	 * 사용자 검색
	 * @param user
	 * @param nickName
	 * @return
	 */
	ReturnBasic searchInviteUser(LoginUser user, String nickName);

	/**
	 * 확인 안한 알람이 있는지 확인
	 * @param user
	 * @return
	 */
	ReturnBasic getMyRedDot(LoginUser user);
}
