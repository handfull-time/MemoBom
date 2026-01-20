package com.utime.memoBom.board.dao;

import java.util.List;

import com.utime.memoBom.board.vo.BoardReqVo;
import com.utime.memoBom.board.vo.CommentItem;
import com.utime.memoBom.board.vo.FragmentItem;
import com.utime.memoBom.board.vo.FragmentListReqVO;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.vo.UserVo;

public interface BoardDao {
	
	/**
	 * Fragment(편린) 저장
	 * @param reqVo
	 * @return
	 */
	int saveFragment(UserVo user, UserDevice device, BoardReqVo reqVo) throws Exception;

	/**
	 * 목록 갖고 오기
	 * @param user
	 * @param reqVo
	 * @return
	 */
	List<FragmentItem> loadFragmentList(UserVo user, FragmentListReqVO reqVo);

	/**
	 * 뎃글 목록 얻기
	 * @param user
	 * @param uid
	 * @param pageNo
	 * @return
	 */
	List<CommentItem> loadCommentsList(UserVo user, String uid, int pageNo);
}
