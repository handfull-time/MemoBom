package com.utime.memoBom.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.admin.vo.DashboardVo;

@Mapper
public interface AdminDashBoardMapper {

	DashboardVo getDashboard(@Param("date") String date);
}
