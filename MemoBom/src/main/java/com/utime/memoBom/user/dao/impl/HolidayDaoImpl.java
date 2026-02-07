package com.utime.memoBom.user.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.memoBom.user.dao.HolidayDao;
import com.utime.memoBom.user.mapper.HolidayMapper;
import com.utime.memoBom.user.vo.HolidayVo;

@Repository
class HolidayDaoImpl implements HolidayDao {

	@Autowired
	private HolidayMapper holidayMapper;
	
	@Override
	@Transactional(rollbackFor = {Exception.class})
	public int insertHolidayList(List<HolidayVo> holiList) throws Exception {

		int insertedCount = 0;
		
		for( HolidayVo holi : holiList ) {
			insertedCount += holidayMapper.insertHoliday(holi);
		}
		
		return insertedCount;
	}

}
