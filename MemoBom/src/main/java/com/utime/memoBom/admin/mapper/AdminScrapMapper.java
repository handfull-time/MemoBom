package com.utime.memoBom.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.utime.memoBom.admin.vo.AdminScrapVo;
import com.utime.memoBom.admin.vo.AdminSearchVo;

@Mapper
public interface AdminScrapMapper {
	List<AdminScrapVo> getScrapList(AdminSearchVo searchVo);
}
