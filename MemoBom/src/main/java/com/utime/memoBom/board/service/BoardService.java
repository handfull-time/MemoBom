package com.utime.memoBom.board.service;

import com.utime.memoBom.board.vo.BoardReqVo;
import com.utime.memoBom.board.vo.FragmentListReqVO;
import com.utime.memoBom.board.vo.TopicVo;
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

}
