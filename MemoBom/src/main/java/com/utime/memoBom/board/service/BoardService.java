package com.utime.memoBom.board.service;

import com.utime.memoBom.board.vo.BoardReqVo;
import com.utime.memoBom.board.vo.CommentReqVo;
import com.utime.memoBom.board.vo.EmotionReqVo;
import com.utime.memoBom.board.vo.FragmentListReqVO;
import com.utime.memoBom.board.vo.ShareVo;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;

public interface BoardService {


	/**
	 * 키 생성
	 * @param request
	 * @return
	 */
	String createKey(HttpServletRequest request, UserVo user);
	
	/**
	 * Fragment(편린) 저장
	 * @param reqVo
	 * @return
	 */
	ReturnBasic saveFragment(UserVo user, UserDevice device, BoardReqVo reqVo);

	/**
	 * 목록 갖고 오기
	 * @param user
	 * @param reqVo
	 * @return
	 */
	ReturnBasic loadFragmentList(UserVo user, FragmentListReqVO reqVo);

	/**
	 * 뎃글 목록 얻기
	 * @param user
	 * @param uid
	 * @param pageNo
	 * @return
	 */
	ReturnBasic loadCommentsList(UserVo user, String uid, int pageNo);

	/**
	 * 스크랩 처리
	 * @param user
	 * @param fragmentUid
	 * @return
	 */
	ReturnBasic procScrap(UserVo user, String fragmentUid);

	/**
	 * 감정 처리
	 * @param user
	 * @param emotionReqVo
	 * @return
	 */
	ReturnBasic procEmotion(UserVo user, EmotionReqVo emotionReqVo);

	/**
	 * 공유 정보 로드
	 * @param user
	 * @param uid
	 * @return
	 */
	ShareVo loadShareInfo(UserVo user, String uid)throws Exception;

	/**
	 * 댓글 저장
	 * @param user
	 * @param reqVo
	 * @return
	 */
	ReturnBasic saveComment(UserVo user, CommentReqVo reqVo);

}
