package com.utime.memoBom.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.board.dto.EmotionDto;
import com.utime.memoBom.board.vo.CommentItem;
import com.utime.memoBom.board.vo.CommentReqVo;
import com.utime.memoBom.board.vo.EmotionItem;
import com.utime.memoBom.board.vo.FragmentImageVo;
import com.utime.memoBom.board.vo.FragmentItem;
import com.utime.memoBom.board.vo.FragmentListReqVO;
import com.utime.memoBom.board.vo.FragmentVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.board.vo.query.MyCommentVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.UserDevice;

/**
 * 게시글 처리
 */
@Mapper
public interface BoardMapper {

	/**
	 * Fragment(편린) 저장
	 * @param reqVo
	 * @return
	 */
	int insertFragment(@Param("user") LoginUser user, @Param("device") UserDevice device, @Param("topic") TopicVo topic, @Param("req") FragmentVo reqVo);
	
	/** * 해시태그 병합
	 * @param tagName
	 * @return
	 */
	int mergeFragmentHashTag( @Param("name") String tagName );
	
	/**
	 * 해시태그-편린 연결 병합
	 * @param name
	 * @param fragmentNo
	 * @return
	 */
	int mergeFragmentHashTagRecordByName( @Param("name")String name, @Param("fragmentNo") long fragmentNo );
	
	/**
     * 게시글 목록 조회
     * @param user 현재 사용자 정보
     * @param reqVo 검색 조건
     * @return FragmentItem 리스트
     */
    List<FragmentItem> loadFragmentList( @Param("user") LoginUser user, @Param("req") FragmentListReqVO reqVo );
    
    /**
     * 게시글 정보 조회
     * @param no
     * @param uid
     * @return
     */
    FragmentItem getFragment( @Param("fNo") long no, @Param("fUid") String uid);

    /**
     * 스크랩 존재 여부 확인
     * @param userNo
     * @param fragmentUid
     * @return
     */
	boolean existsScrap(@Param("userNo") long userNo, @Param("fUid") String fragmentUid);

	/**
	 * 스크랩 삭제
	 * @param userNo
	 * @param fragmentUid
	 * @return
	 */
	int deleteScrap(@Param("userNo") long userNo, @Param("fUid") String fragmentUid);

	/**
	 * 스크랩 추가
	 * @param userNo
	 * @param fragmentUid
	 * @return
	 */
	int insertScrap(@Param("userNo") long userNo, @Param("fUid") String fragmentUid);
	
	/**
	 * 편린 감정 목록 조회
	 * @param uid
	 * @return
	 */
	List<EmotionItem> selectEmotionList(@Param("req") EmotionDto emotionReqVo);

	/**
	 * 감정 삽입 또는 업데이트
	 * @param userNo
	 * @param req
	 * @return
	 */
	int upsertEmotion(@Param("userNo") long userNo, @Param("req") EmotionDto emotionReqVo);
	
	/**
	 * 감정 삭제
	 * @param userNo
	 * @param req
	 * @return
	 */
	int deleteEmotion(@Param("userNo") long userNo, @Param("req") EmotionDto emotionReqVo);
	
	/**
	 * 댓글 저장
	 * @param user
	 * @param reqVo
	 * @return
	 */
	int insertComment(@Param("user") LoginUser user, @Param("req") CommentReqVo reqVo);

	/**
	 * 댓글 조회
	 * @param commentNo
	 * @return
	 */
	CommentItem selectCommentByNo(long commentNo);
	
	/**
	 * user 작성한 FRAGMENT 목록 조회
	 * @param user
	 * @param keyword
	 * @param pageNo
	 * @return
	 */
	List<FragmentItem> listMyFragments(@Param("user") LoginUser user, @Param("keyword") String keyword, @Param("pageNo") int pageNo);
	
	/**
	 * 댓글 목록 얻기
	 * @param user
	 * @param uid
	 * @param pageNo
	 * @return
	 */
	List<CommentItem> loadCommentsList(@Param("uid") String uid, @Param("pageNo") int pageNo);
	
	/**
	 * user 작성한 댓글 목록 조회
	 * @param user
	 * @param keyword
	 * @param pageNo
	 * @return
	 */
	List<MyCommentVo> listMyComments(@Param("user") LoginUser user, @Param("keyword") String keyword, @Param("pageNo") int pageNo);
	
	/**
	 * Fragment 목록
	 * @param user
	 * @param keyword
	 * @param pageNo
	 * @return
	 */
	List<FragmentItem> listMyScrapFragments(@Param("user") LoginUser user, @Param("keyword") String keyword, @Param("pageNo") int pageNo);

	/**
	 * Fragment 제거
	 * @param userNo
	 * @param uid
	 * @return
	 */
	int removeFragment(@Param("userNo") long userNo, @Param("uid") String uid);

	/**
	 * Fragment 수정
	 * @param userNo
	 * @param item
	 * @return
	 */
	int updateFragment(@Param("userNo") long userNo, @Param("req") FragmentVo item);

	/**
	 * 이미지 정보 적용
	 * @param user
	 * @param topic
	 * @param imgVo
	 * @return
	 */
	int upsertFragmentImage(@Param("user") LoginUser user, @Param("fragment") long fragmentNo, @Param("item") FragmentImageVo imgVo);
}
