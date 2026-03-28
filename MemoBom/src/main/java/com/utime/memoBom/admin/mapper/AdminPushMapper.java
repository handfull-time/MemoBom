package com.utime.memoBom.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.admin.vo.AdminPushVo;
import com.utime.memoBom.admin.vo.AdminSearchVo;

@Mapper
public interface AdminPushMapper {
	List<AdminPushVo> getPushList(AdminSearchVo searchVo);
	int deleteInactivePushSubs(@Param("day") int day);
}
