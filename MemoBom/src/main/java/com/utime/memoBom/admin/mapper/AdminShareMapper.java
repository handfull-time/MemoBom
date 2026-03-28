package com.utime.memoBom.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.utime.memoBom.admin.vo.AdminSearchVo;
import com.utime.memoBom.admin.vo.AdminShareVo;

@Mapper
public interface AdminShareMapper {
	List<AdminShareVo> getShareList(AdminSearchVo searchVo);
}
