package com.utime.memoBom.admin.dao;

import java.util.List;

import com.utime.memoBom.admin.vo.AdminCommentVo;
import com.utime.memoBom.admin.vo.AdminEmotionVo;
import com.utime.memoBom.admin.vo.AdminFragmentVo;
import com.utime.memoBom.admin.vo.AdminPushVo;
import com.utime.memoBom.admin.vo.AdminScrapVo;
import com.utime.memoBom.admin.vo.AdminSearchVo;
import com.utime.memoBom.admin.vo.AdminShareVo;
import com.utime.memoBom.admin.vo.AdminTopicVo;
import com.utime.memoBom.admin.vo.AdminUserVo;
import com.utime.memoBom.admin.vo.DashboardVo;

public interface AdminDao {

	/**
	 * date 날짜에 발생한 전체 내용 조회
	 * @param date
	 * @return
	 */
	DashboardVo getDashboard(String date);
	
	List<AdminUserVo> getUserList(AdminSearchVo searchVo);

	List<AdminTopicVo> getTopicList(AdminSearchVo searchVo);

	List<AdminFragmentVo> getFragmentList(AdminSearchVo searchVo);

	List<AdminCommentVo> getCommentList(AdminSearchVo searchVo);

	List<AdminEmotionVo> getEmotionList(AdminSearchVo searchVo);

	List<AdminScrapVo> getScrapList(AdminSearchVo searchVo);

	List<AdminShareVo> getShareList(AdminSearchVo searchVo);

	List<AdminPushVo> getPushList(AdminSearchVo searchVo);


}
