package com.utime.memoBom.user.mapper;

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
	
}
