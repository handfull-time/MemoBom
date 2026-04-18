package com.utime.memoBom.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.admin.vo.AdminFragmentVo;
import com.utime.memoBom.admin.vo.AdminSearchVo;

@Mapper
public interface AdminFragmentMapper {
	List<AdminFragmentVo> getFragmentList(AdminSearchVo searchVo);
	int deleteFragmentsImage(@Param("day") int day);
	int deleteFragments(@Param("day") int day);
}
