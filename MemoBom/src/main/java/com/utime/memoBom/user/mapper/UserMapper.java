package com.utime.memoBom.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.vo.UserVo;

/**
 * 사용자 처리
 */
@Mapper
public interface UserMapper {
	
	/**
	 * 회원 테이블 생성
	 * @return
	 */
	int createUser();
	
	/**
	 * 로그인 기록
	 * @return
	 */
	int createLoginRecord();
	
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
}
