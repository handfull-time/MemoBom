package com.utime.memoBom.user.service;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.dto.MySearchDto;
import com.utime.memoBom.user.vo.UserVo;

public interface UserService {

	/**
	 * 사용자 정보 조회
	 * @param uid
	 * @return
	 */
	UserVo getUserFromUid(String uid);

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
	 * fragment  목록
	 * @param user
	 * @param searchVo
	 * @return
	 */
	ReturnBasic getMyFragmentsDataList(LoginUser user, MySearchDto searchVo);

	/**
	 * mosaic  목록
	 * @param user
	 * @param searchVo
	 * @return
	 */
	ReturnBasic getMyMosaicDataList(LoginUser user, MySearchDto searchVo);

	/**
	 * 뎃글 목록
	 * @param user
	 * @param searchVo
	 * @return
	 */
	ReturnBasic getMyCommentsDataList(LoginUser user, MySearchDto searchVo);

}
