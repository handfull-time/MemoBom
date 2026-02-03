package com.utime.memoBom.board.service;

import com.utime.memoBom.board.dto.BoardReqDto;
import com.utime.memoBom.board.dto.EmotionDto;
import com.utime.memoBom.board.dto.FragmentListDto;
import com.utime.memoBom.board.vo.CommentReqVo;
import com.utime.memoBom.board.vo.EmojiSetType;
import com.utime.memoBom.board.vo.FragmentListReqVO;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.common.vo.UserDevice;

import jakarta.servlet.http.HttpServletRequest;

public interface BoardService {


	/**
	 * 키 생성
	 * @param request
	 * @return
	 */
	String createKey(HttpServletRequest request, LoginUser user);
	
	/**
	 * Fragment(편린) 저장
	 * @param reqVo
	 * @return
	 */
	ReturnBasic saveFragment(LoginUser user, UserDevice device, BoardReqDto reqVo);

	/**
	 * 목록 갖고 오기
	 * @param user
	 * @param reqVo
	 * @return
	 */
	FragmentListDto loadFragmentList(LoginUser user, FragmentListReqVO reqVo);

	/**
	 * 뎃글 목록 얻기
	 * @param user
	 * @param uid
	 * @param pageNo
	 * @return
	 */
	ReturnBasic loadCommentsList(LoginUser user, String uid, int pageNo, EmojiSetType emojiSetType);

	/**
	 * 스크랩 처리
	 * @param user
	 * @param fragmentUid
	 * @return
	 */
	ReturnBasic procScrap(LoginUser user, String fragmentUid);

	/**
	 * 감정 처리
	 * @param user
	 * @param emotionReqVo
	 * @return
	 */
	ReturnBasic procEmotion(LoginUser user, EmotionDto emotionReqVo);

	/**
	 * 댓글 저장
	 * @param user
	 * @param reqVo
	 * @return
	 */
	ReturnBasic saveComment(LoginUser user, CommentReqVo reqVo);

}
