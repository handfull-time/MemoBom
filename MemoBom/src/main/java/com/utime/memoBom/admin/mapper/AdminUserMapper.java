package com.utime.memoBom.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.admin.vo.AdminSearchVo;
import com.utime.memoBom.admin.vo.AdminUserVo;

@Mapper
public interface AdminUserMapper {

	List<AdminUserVo> getUserList(AdminSearchVo searchVo);

	int updateUserNote(@Param("userNo") Long userNo, @Param("note") String note);

	int deleteUser(@Param("userNo") Long userNo);
}
