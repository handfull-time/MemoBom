package com.utime.memoBom.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.vo.MyWriterVo;
import com.utime.memoBom.user.vo.UserVo;
import com.utime.memoBom.user.vo.query.BasicUserVo;
import com.utime.memoBom.user.vo.query.UsageStatisticsVo;

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
	 * 사용자 정보 조회
	 */
	UserVo selectUserFromUid(@Param("uid")String uid);
	
	/**
	 * 사용자 정보 조회
	 */
	UserVo selectUserFromUserNo(@Param("userNo")long userNo);
	
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
	
	/**
	 * 사용자 이용 통계
	 * @param userNo
	 * @return
	 */
	UsageStatisticsVo selectUserRecord( long userNo );

	/**
	 * nickName 변경
	 */
	int updateNicname(@Param("user")LoginUser user, @Param("nickname")String nickname);

	/**
	 * 개인 이미지 정보 수정
	 * @param user
	 * @param profile
	 * @return
	 */
	int updateProfile(@Param("user")LoginUser user, @Param("profile") String profile);
	
	/**
	 * 푸시 수신 상태
	 * @param user
	 * @return
	 */
	boolean selectPushStatus(@Param("user")LoginUser user);

	/**
	 * 푸시 수신 설정
	 * @param user
	 * @param enabled true:수신, false:미수신
	 * @return
	 */
	int updatePushStatus(@Param("user")LoginUser user, @Param("enabled")boolean enabled);
}
