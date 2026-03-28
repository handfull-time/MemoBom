package com.utime.memoBom.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.admin.vo.AdminCommentVo;
import com.utime.memoBom.admin.vo.AdminFragmentVo;
import com.utime.memoBom.admin.vo.AdminSearchVo;

@Mapper
public interface AdminCommentMapper {
	List<AdminCommentVo> getCommentList(AdminSearchVo searchVo);
	AdminFragmentVo getFragmentPreview(@Param("fragmentNo") Long fragmentNo);
	int updateCommentDeleted(@Param("commentNo") Long commentNo, @Param("deleted") Boolean deleted);
	int deleteComments(@Param("day") int day);
}
