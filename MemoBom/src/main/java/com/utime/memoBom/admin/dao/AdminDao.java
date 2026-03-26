package com.utime.memoBom.admin.dao;

import com.utime.memoBom.admin.vo.gemini.DashboardVo;

public interface AdminDao {

	/**
	 * date 날짜에 발생한 전체 내용 조회
	 * @param date
	 * @return
	 */
	DashboardVo getDashboard(String date);

}
