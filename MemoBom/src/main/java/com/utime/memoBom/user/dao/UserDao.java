package com.utime.memoBom.user.dao;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.vo.MyWriterVo;
import com.utime.memoBom.user.vo.UserVo;
import com.utime.memoBom.user.vo.query.BasicUserVo;
import com.utime.memoBom.user.vo.query.UsageStatisticsVo;
import com.utime.memoBom.user.vo.query.UserProfile;

public interface UserDao {

	/**
	 * 사용자 계정 찾기
	 * @param provider
	 * @param id
	 * @return
	 */
	UserVo findById(String provider, String id);
	
	/**
	 * 사용자 추가
	 * @param user
	 * @return
	 * @throws Exception
	 */
	int addUser( UserVo user ) throws Exception;

	/**
	 * 로그인 기록 추가
	 * @param result
	 * @param ip
	 * @param device
	 * @return
	 * @throws Exception
	 */
	int addLoginRecord(UserVo result, String ip, UserDevice device) throws Exception;

	/**
	 * 회원 제거
	 * @param userVo
	 * @return
	 * @throws Exception
	 */
	int deleteUser(UserVo userVo)throws Exception;
	
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
	List<MyWriterVo> getMyWriteDataList(LoginUser user, String date);

	/**
	 * 단순 사용자 정보 조회
	 * @param userNo
	 * @return
	 */
	BasicUserVo getBasicUserFromUserNo(long userNo);
	
	/**
	 * 사용자 이용 통계
	 * @param userNo
	 * @return
	 */
	UsageStatisticsVo getUserStatisticsRecord( long userNo );

	/**
	 * 개인 정보 수정
	 * @param user
	 * @param nickname
	 * @param profileUrl
	 * @return
	 */
	int updateUserInfo(LoginUser user, String nickname, MultipartFile profile) throws Exception;
	
	/**
	 * 사용자 이미지 정보
	 * @param uid
	 * @return
	 */
	UserProfile getUserProfile(String uid);
}
