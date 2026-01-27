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

	/**
	 * 특정 연도의 공휴일 정보가 존재하는지 확인
	 * @param year
	 * @return
	 */
	boolean hasHolidayList(int year);
}
