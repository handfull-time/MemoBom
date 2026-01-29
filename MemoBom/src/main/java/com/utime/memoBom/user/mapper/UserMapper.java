package com.utime.memoBom.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.vo.MyWriterVo;
import com.utime.memoBom.user.vo.UserVo;
import com.utime.memoBom.user.vo.query.BasicUserVo;

/**
 * 사용자 처리
 */
@Mapper
public interface UserMapper {
	
	
	/**
	 * 회원 검색
	 * @param provider
	 * @param id
	 * @return
	 */
	UserVo selectUserFromIdAndProvider(@Param("provider")String provider, @Param("id")String id);
	
	/**
	 * 회원 추가
	 * @param user
	 * @return
	 */
	int insertUser(UserVo user);
	
	/**
	 * 로그인 기록 추가
	 * @param user
	 * @param ip
	 * @param device
	 * @return
	 */
	int insertLoginRecord(@Param("user")UserVo user, @Param("ip")String ip, @Param("device")UserDevice device);

	/**
	 * 회원 정보 제거
	 * @param userVo
	 * @return
	 */
	int removeUser(UserVo userVo);

	/**
	 * 사용자 정보 조회
	 */
	UserVo selectUserFromUid(@Param("uid")String uid);
	
	/** 
	 * 내 작성 데이터
	 * @param user
	 * @param date yyyyMM
	 * @return
	 */
	List<MyWriterVo> selectMyWriteDataList(@Param("user")LoginUser user, @Param("date")String date);
	
	/**
	 * 단순 사용자 정보 조회
	 * @param userNo
	 * @return
	 */
	BasicUserVo getBasicUserFromUserNo(long userNo);
}
