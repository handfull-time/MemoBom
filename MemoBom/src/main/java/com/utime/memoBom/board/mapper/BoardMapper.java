package com.utime.memoBom.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.board.vo.FragmentItem;
import com.utime.memoBom.board.vo.FragmentVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.vo.UserVo;

/**
 * 게시글 처리
 */
@Mapper
public interface BoardMapper {

	int upsertEmotion(long userNo, int targetType, long targetNo, String emotion);
	
	int deleteEmotion(long userNo, int targetType, long targetNo);
	
	/**
	 * Fragment(편린) 저장
	 * @param reqVo
	 * @return
	 */
	int insertFragment(@Param("user") UserVo user, @Param("device") UserDevice device, @Param("topic") TopicVo topic, @Param("req") FragmentVo reqVo);
	
	int mergeFragmentHashTag( @Param("name") String tagName );
	
	int mergeFragmentHashTagRecordByName( @Param("name")String name, @Param("fragmentNo") long fragmentNo );
	
	/**
     * 게시글 목록 조회 (스크랩 여부 확인 포함)
     * * @param currentUserUid 현재 로그인한 사용자의 UID (스크랩 여부 확인용, nullable)
     * @param userUid 작성자 필터링용 UID (nullable)
     * @param topicUid 토픽 필터링용 UID (nullable)
     * @param keyword 검색 키워드 (nullable)
     * @param pageNo 페이지 번호 (1부터 시작)
     * @return FragmentItem 리스트
     */
    List<FragmentItem> loadList(
    	@Param("user") UserVo user,
        @Param("userUid") String userUid, 
        @Param("topicUid") String topicUid, 
        @Param("keyword") String keyword, 
        @Param("pageNo") int pageNo
    );
}
