package com.utime.memoBom.user.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;

import com.utime.memoBom.user.vo.HolidayVo;

/**
 * 사용자 처리
 */
@Mapper
public interface HolidayMapper {

	/**
	 * 공휴일 추가.
	 * @param holiList
	 * @return
	 * @throws Exception
	 */
	int insertHoliday(HolidayVo holiList);
	
	/**
	 * 가장 마지막 업데이트 시간
	 * @return
	 */
	LocalDateTime selectLastUpdateTime();
	
}
