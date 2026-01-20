package com.utime.memoBom.board.service.impl;

import org.springframework.stereotype.Service;

import com.utime.memoBom.board.dao.BoardDao;
import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.service.BoardService;
import com.utime.memoBom.board.vo.BoardReqVo;
import com.utime.memoBom.board.vo.FragmentListReqVO;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.dao.KeyValueDao;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class BoardServiceImpl implements BoardService {

	final BoardDao boardDao;
	
	final TopicDao topicDao;
	
	final KeyValueDao keyValueDao;
	
	@Override
	public ReturnBasic saveFragment(UserVo user, UserDevice device, BoardReqVo reqVo) {
		
		final ReturnBasic keyRes = KeyUtil.checkKey(keyValueDao, user, device, reqVo.getSeal());
		if( keyRes.isError() ) {
			return keyRes;
		}
		
		// 오른쪽 공백 제거
		reqVo.setContent( reqVo.getContent().stripTrailing() );
		
		if( AppUtils.isEmpty(reqVo.getContent())) {
			log.warn("냉무 ...");
			return new ReturnBasic("E", "잘못된 요청 입니다.");
		}
		
		final ReturnBasic result = new ReturnBasic();
		
		try {
			boardDao.saveFragment(user, device, reqVo);
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "An error occurred while saving.");
		}
		
		return result;
	}

	@Override
	public String createKey(HttpServletRequest request, UserVo user) {

		return KeyUtil.createKey(keyValueDao, request, user);
	}

	@Override
	public ReturnBasic loadFragmentList(UserVo user, FragmentListReqVO reqVo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		try {
			result.setData( boardDao.loadFragmentList(user, reqVo) );
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "An error occurred while saving.");
		}
		
		return result;
	}
	
	@Override
	public ReturnBasic loadCommentsList(UserVo user, String uid, int pageNo) {
		
		final ReturnBasic result = new ReturnBasic();
		
		try {
			result.setData( boardDao.loadCommentsList(user, uid, pageNo) );
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "An error occurred while saving.");
		}
		
		return result;
	}
}
