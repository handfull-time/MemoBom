package com.utime.memoBom.board.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.utime.memoBom.board.dao.BoardDao;
import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.service.BoardService;
import com.utime.memoBom.board.vo.BoardReqVo;
import com.utime.memoBom.board.vo.CommentReqVo;
import com.utime.memoBom.board.vo.EmotionReqVo;
import com.utime.memoBom.board.vo.FragmentListReqVO;
import com.utime.memoBom.board.vo.ShareVo;
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
	
	@Override
	public ReturnBasic procEmotion(UserVo user, EmotionReqVo emotionReqVo) {
		final ReturnBasic result = new ReturnBasic();
		
		try {
			result.setData( boardDao.procEmotion(user, emotionReqVo) );
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "Error during emotion processing.");
		}
		
		return result;
	}
	
	@Override
	public ReturnBasic procScrap(UserVo user, String fragmentUid) {
		final ReturnBasic result = new ReturnBasic();
		
		try {
			Boolean isScrapped = boardDao.procScrap(user, fragmentUid);
			if( isScrapped == null ) {
				result.setCodeMessage("E", "Failed to process scrap.");
			}else {
				result.setData( isScrapped );
			}
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "An error occurred while saving.");
		}
		
		return result;
	}
	
	@Value("${appName}")
	private String appName;
	
	@Override
	public ShareVo loadShareInfo(UserVo user, String uid) throws Exception{
		
		final ShareVo result = boardDao.addShareInfo(user, uid);
		
		result.setTitle(this.appName + " - Shared");
		
		return result;
	}

	@Override
	public ReturnBasic saveComment(UserVo user, CommentReqVo reqVo) {
		
		ReturnBasic result = new ReturnBasic();
		
		try {
			result.setData( boardDao.saveComment(user, reqVo) );
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "An error occurred while saving.");
		}

		return result;
	}
}
