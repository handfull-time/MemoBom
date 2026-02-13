package com.utime.memoBom.admin.mapper;

import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.push.vo.PushSendDataVo;

/**
 * 게시글 처리
 */
@Mapper
public interface AdminAlarmMapper {
	
	/**
	 * 푸시로 알람 내역 추가.
	 * @param user
	 * @param data
	 * @return
	 */
	int insertPushAlarm( @Param("user") LoginUser user, @Param("push") PushSendDataVo data, @Param("uid") UUID uid );
	
	/**
	 * 알람 읽음 처리.
	 * @param uid
	 * @return
	 */
	int updateAlarmCheck(@Param("uid") String uid );
}
