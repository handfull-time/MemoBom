package com.utime.memoBom.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.utime.memoBom.admin.vo.AdminSearchVo;
import com.utime.memoBom.admin.vo.AdminTopicVo;

@Mapper
public interface AdminTopicMapper {

	List<AdminTopicVo> getTopicList(AdminSearchVo searchVo);

	int updateTopicEditable(AdminTopicVo topicVo);
}
