package com.utime.memoBom.board.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.utime.memoBom.board.dao.BoardDao;
import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.dto.BoardReqDto;
import com.utime.memoBom.board.dto.EmotionDto;
import com.utime.memoBom.board.dto.FragmentDto;
import com.utime.memoBom.board.dto.FragmentListDto;
import com.utime.memoBom.board.service.BoardService;
import com.utime.memoBom.board.vo.CommentReqVo;
import com.utime.memoBom.board.vo.EmojiSetType;
import com.utime.memoBom.board.vo.FragmentItem;
import com.utime.memoBom.board.vo.FragmentListReqVO;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.dao.KeyValueDao;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.BinResultVo;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.push.service.PushSendService;
import com.utime.memoBom.user.dao.UserDao;
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
	
	final UserDao userDao;
	
	final KeyValueDao keyValueDao;
	
	final PushSendService pushService; 
	
	@Override
	public ReturnBasic saveFragment(LoginUser user, UserDevice device, BoardReqDto reqVo) {
		
		final ReturnBasic keyRes = KeyUtil.checkKey(keyValueDao, user, device, reqVo.getSeal());
		if( keyRes.isError() ) {
			return keyRes;
		}
		
		// 오른쪽 공백 제거
		reqVo.setContent( reqVo.getContent().stripTrailing() );
		
		if( AppUtils.isEmpty(reqVo.getContent())) {
			log.warn("냉무 ...");
			return new ReturnBasic("E", "내용이 없습니다.");
		}
		
		final ReturnBasic result = new ReturnBasic();
		
		try {
			boardDao.saveFragment(user, device, reqVo);
			if( AppUtils.isEmpty( reqVo.getUid() )) {
				// 새글만 push 보냄.
				pushService.sendMessageNewFragment( user, reqVo.getTopicUid() );
			}			 
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "An error occurred while saving.");
		}
		
		return result;
	}

	@Override
	public String createKey(HttpServletRequest request, LoginUser user) {

		return KeyUtil.createKey(keyValueDao, request, user);
	}

	@Override
	public FragmentDto loadFragment( LoginUser user, String uid) {
		
		final FragmentDto result;
		final FragmentItem item = boardDao.loadFragment( user, uid);
		if( item == null ) {
			log.info("{} 데이터 없음", uid);
			result = null;
		}else {
			result = FragmentDto.of(item);
		}
		
		return result;
	}
	
	@Override
	public FragmentListDto loadFragmentList(HttpServletRequest request, LoginUser user, FragmentListReqVO reqVo) {
		
		final FragmentListDto result = new FragmentListDto();
		
		final String baseUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath();
		
		try {
			final List<FragmentItem> list = boardDao.loadFragmentList(user, reqVo);

			final List<FragmentDto> resultList = new ArrayList<>();
			result.setData( resultList );
			
			for( FragmentItem item : list ) {
				final FragmentDto addItem = FragmentDto.of(item);
				addItem.setContent( SimpleLinkRenderer.render(addItem.getContent(), baseUrl) );
				resultList.add( addItem );
			}
			
			if( reqVo.getPageNo() == 1 ) {
				final String search = " 검색";
				if( AppUtils.isNotEmpty( reqVo.getUserUid() ) ) {
					final UserVo vo = userDao.getUserFromUid( reqVo.getUserUid() );
					result.setTitle( vo == null ? "Art":vo.getNickname() + search);
				} else if( AppUtils.isNotEmpty( reqVo.getFragUid() ) ) {
					final FragmentItem vo = boardDao.loadFragment(user, reqVo.getFragUid());
					final String content = (vo == null)? "Fragment":vo.getContent();
					result.setTitle( (content.length()>6? content.substring(0, 6):content) + search);
				} else if( AppUtils.isNotEmpty( reqVo.getTopicUid() ) ) {
					final TopicVo vo = topicDao.loadTopic(user, reqVo.getTopicUid());
					final String name = (vo == null)? "Mosaic":vo.getName();
					result.setTitle( (name.length()>6? name.substring(0, 6):name) + search);
				} else if( AppUtils.isNotEmpty( reqVo.getCmtUid() ) ) {
					result.setTitle( "댓글" + search  );
				} else if( AppUtils.isNotEmpty( reqVo.getKeyword() ) ) {
					result.setTitle( reqVo.getKeyword() + search );
				} else if( AppUtils.isNotEmpty( reqVo.getHashtag() ) ) {
					result.setTitle( reqVo.getHashtag() + search );
				} else {
					result.setTitle( "Fragment" );
				}
			}
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "An error occurred while saving.");
		}
		
		return result;
	}
	
	@Override
	public ReturnBasic loadCommentsList(LoginUser user, String uid, int pageNo, EmojiSetType emojiSetType) {
		
		final ReturnBasic result = new ReturnBasic();
		
		try {
			result.setData( boardDao.loadCommentsList(user, uid, pageNo, emojiSetType) );
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "An error occurred while saving.");
		}
		
		return result;
	}
	
	@Override
	public ReturnBasic procEmotion(LoginUser user, EmotionDto emotionReqVo) {
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
	public ReturnBasic procScrap(LoginUser user, String fragmentUid) {
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

	@Override
	public ReturnBasic saveComment(LoginUser user, CommentReqVo reqVo) {
		
		ReturnBasic result = new ReturnBasic();
		
		try {
			result.setData( boardDao.saveComment(user, reqVo) );
		} catch (Exception e) {
			log.error("", e);
			result.setCodeMessage("E", "An error occurred while saving.");
		}

		return result;
	}
	
	@Override
	public BinResultVo getImage(boolean isThumb, String uid) {
		
		return boardDao.getImage(isThumb, uid);
	}
}
