package com.utime.memoBom.user.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.utime.memoBom.user.vo.HolidayVo;

public interface HolidayDao {

	/**
	 * 공휴일 목록 일괄 삽입
	 * @param holiList
	 * @return
	 * @throws Exception
	 */
	int insertHolidayList( List<HolidayVo> holiList) throws Exception;

	/**
	 * 가장 마지막 업데이트 시간
	 * @return
	 */
	LocalDateTime getLastUpdateTime();

}
