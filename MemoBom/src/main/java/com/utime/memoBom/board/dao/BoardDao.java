package com.utime.memoBom.board.dao;

import java.util.List;

import com.utime.memoBom.board.dto.BoardReqDto;
import com.utime.memoBom.board.dto.EmotionDto;
import com.utime.memoBom.board.vo.CommentItem;
import com.utime.memoBom.board.vo.CommentReqVo;
import com.utime.memoBom.board.vo.EmojiSetType;
import com.utime.memoBom.board.vo.EmotionItem;
import com.utime.memoBom.board.vo.FragmentItem;
import com.utime.memoBom.board.vo.FragmentListReqVO;
import com.utime.memoBom.board.vo.query.MyCommentVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.BinResultVo;
import com.utime.memoBom.common.vo.UserDevice;

public interface BoardDao {
	
	/**
	 * Fragment(편린) 저장
	 * @param reqVo
	 * @return
	 */
	int saveFragment(LoginUser user, UserDevice device, BoardReqDto reqVo) throws Exception;

	/**
	 * 목록 갖고 오기
	 * @param user
	 * @param reqVo
	 * @return
	 */
	List<FragmentItem> loadFragmentList(LoginUser user, FragmentListReqVO reqVo);
	
	/**
	 * Fragment uid 조회
	 * @param fUid
	 * @return
	 */
	FragmentItem loadFragment(LoginUser user, String fUid);
	

	/**
	 * 뎃글 목록 얻기
	 * @param user
	 * @param uid
	 * @param pageNo
	 * @return
	 */
	List<CommentItem> loadCommentsList(LoginUser user, String uid, int pageNo, EmojiSetType emojiSetType);

	/**
	 * 스크랩 처리
	 * @param user
	 * @param fragmentUid
	 * @return null이면 실패, true면 스크랩, false면 스크랩 취소
	 */
	Boolean procScrap(LoginUser user, String fragmentUid) throws Exception;
	
	/**
	 * 감정 처리
	 * @param user
	 * @param emotionReqVo
	 * @return
	 */
	List<EmotionItem> procEmotion(LoginUser user, EmotionDto emotionReqVo) throws Exception;
	
	/**
	 * user 작성한 FRAGMENT 목록 조회
	 * @param user
	 * @param keyword
	 * @param pageNo
	 * @return
	 */
	List<FragmentItem> listMyFragments(LoginUser user, String keyword, int pageNo);
	
	
	/**
	 * 댓글 저장
	 * @param user
	 * @param reqVo
	 * @return
	 */
	CommentItem saveComment(LoginUser user, CommentReqVo reqVo) throws Exception;
	
	/**
	 * user 작성한 댓글 목록 조회
	 * @param user
	 * @param keyword
	 * @param pageNo
	 * @return
	 */
	List<MyCommentVo> listMyComments(LoginUser user, String keyword, int pageNo);
	
	/**
	 * user가 스크랩한 목록
	 * @param user
	 * @param keyword
	 * @param pageNo
	 * @return
	 */
	List<FragmentItem> listMyScrapFragments(LoginUser user, String keyword, int pageNo);

	/**
	 * 이미지 정보 갖고 오기
	 * @param isThumb
	 * @param uid
	 * @return
	 */
	BinResultVo getImage(boolean isThumb, String uid);
}
