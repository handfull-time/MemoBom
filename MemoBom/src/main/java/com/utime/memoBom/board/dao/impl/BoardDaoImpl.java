package com.utime.memoBom.board.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.memoBom.board.dao.BoardDao;
import com.utime.memoBom.board.mapper.BoardMapper;
import com.utime.memoBom.board.mapper.TopicMapper;
import com.utime.memoBom.board.vo.BoardReqVo;
import com.utime.memoBom.board.vo.CommentItem;
import com.utime.memoBom.board.vo.CommentReqVo;
import com.utime.memoBom.board.vo.EEmotionCode;
import com.utime.memoBom.board.vo.EmojiSetType;
import com.utime.memoBom.board.vo.EmojiSets;
import com.utime.memoBom.board.vo.EmotionItem;
import com.utime.memoBom.board.vo.EmotionReqVo;
import com.utime.memoBom.board.vo.FragmentItem;
import com.utime.memoBom.board.vo.FragmentListReqVO;
import com.utime.memoBom.board.vo.FragmentVo;
import com.utime.memoBom.board.vo.ShareVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.vo.UserVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class BoardDaoImpl implements BoardDao {

	final BoardMapper boardMapper;
	final TopicMapper topicMapper;

	/**
	 * 해시태그 문자열을 파싱하여 순수한 키워드 목록을 반환합니다. 특징: Regex 미사용, 1-Pass 파싱, 메모리 할당 최소화
	 *
	 * @param rawInput 사용자가 입력한 원본 문자열
	 * @return 중복이 제거되고 순서가 보장된 태그 Set
	 */
	private Set<String> parseTags(String rawInput) {
		// 1. 순서 보장 및 중복 제거를 위해 LinkedHashSet 사용
		final Set<String> result = new LinkedHashSet<>();

		if (AppUtils.isEmpty(rawInput)) {
			return result;
		}

		StringBuilder buffer = new StringBuilder();

		// 2. 문자열을 char 배열로 변환하여 인덱스 접근 (String.charAt보다 빠름)
		for (char c : rawInput.toCharArray()) {

			// 3. 구분자 체크 (공백, 콤마, 탭, 줄바꿈 등)
			if (c == ' ' || c == ',' || c == '\t' || c == '\n') {
				if (buffer.length() > 0) {
					result.add(buffer.toString());
					buffer.setLength(0); // 버퍼 초기화 (새 객체 생성 X)
				}
			}
			// 4. '#' 문자는 무시 (저장하지 않음)
			else if (c != '#') {
				buffer.append(c);
			}
		}

		// 5. 마지막 버퍼에 남은 단어 처리
		if (buffer.length() > 0) {
			result.add(buffer.toString());
		}

		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveFragment(UserVo user, UserDevice device, BoardReqVo reqVo) throws Exception {

		final FragmentVo item = new FragmentVo();
		item.setContent(reqVo.getContent());
		item.setIp(reqVo.getIp());

		final TopicVo topic = topicMapper.loadTopic(reqVo.getTopicUid(), -1L);
		if (topic == null) {
			throw new Exception("topic is null.");
		}

		int result = 0;
		result += boardMapper.insertFragment(user, device, topic, item);
		result += topicMapper.updateTopicStatsFollowCount( topic.getTopicNo() );

		final Set<String> hashTags = this.parseTags(reqVo.getHashTag());
		if (hashTags.isEmpty()) {
			return result;
		}

		final long fragmentNo = item.getFragmentNo();

		hashTags.stream().forEach(tagName -> {
			boardMapper.mergeFragmentHashTag(tagName);
			boardMapper.mergeFragmentHashTagRecordByName(tagName, fragmentNo);
		});

		return result;
	}

	/**
	 * 이모션 목록을 주어진 이모지 세트에 맞게 정규화합니다.
	 *
	 * @param raw  원본 이모션 목록
	 * @param type 이모지 세트 타입
	 * @return 정규화된 이모션 목록
	 */
	private static List<EmotionItem> normalizeEmotionList(
	        List<EmotionItem> raw,
	        EmojiSetType type 
	) {
	    final Map<EEmotionCode, Integer> map = new EnumMap<>(EEmotionCode.class);
	    if( AppUtils.isNotEmpty(raw)) {
		    for (EmotionItem e : raw) {
		        map.put(e.getEmotion(), e.getCount());
		    }
	    }

	    final List<EmotionItem> result = new ArrayList<>();
	    for (EEmotionCode code : EmojiSets.allowed(type)) {
	    	
	        result.add(new EmotionItem(code, map.getOrDefault(code, 0) ));
	    }
	    
	    return result;
	}

	
	@Override
	public List<FragmentItem> loadFragmentList(UserVo user, FragmentListReqVO reqVo) {
		
		final List<FragmentItem> result  = boardMapper.loadFragmentList(user, reqVo);
		
		if( AppUtils.isNotEmpty(result)) {
			
			result.forEach( fragment -> {
				fragment.setEmotionList( BoardDaoImpl.normalizeEmotionList(
				        fragment.getEmotionList(),
				        fragment.getTopic().getEmojiSetType()
				) );
			});
		}
		
		return result;
	}

	@Override
	public List<CommentItem> loadCommentsList(UserVo user, String uid, int pageNo) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @return true면 스크랩, false면 스크랩 취소
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean procScrap(UserVo user, String fragmentUid) throws Exception {
		
		final boolean exists = boardMapper.existsScrap(user.getUserNo(), fragmentUid);
	    if (exists) {
	    	boardMapper.deleteScrap(user.getUserNo(), fragmentUid);
	    } else {
	    	boardMapper.insertScrap(user.getUserNo(), fragmentUid);
	    }
		return !exists;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ShareVo addShareInfo(UserVo user, String uid) throws Exception {
		
		final ShareVo share = new ShareVo();
		share.setText( boardMapper.selectFragmentContentPreview(uid) );
		
		boardMapper.insertShareInfo(user==null? 0:user.getUserNo(), uid);
		
		return share;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<EmotionItem> procEmotion(UserVo user, EmotionReqVo emotionReqVo) throws Exception {

		int deleted = boardMapper.deleteEmotion(user.getUserNo(), emotionReqVo);
		if (deleted == 0) {
			deleted += boardMapper.upsertEmotion(user.getUserNo(), emotionReqVo);
		}
		
		return BoardDaoImpl.normalizeEmotionList(
				boardMapper.selectEmotionListByFragmentUid(emotionReqVo.getUid()),
				emotionReqVo.getEmojiSetType()
			);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public CommentItem saveComment(UserVo user, CommentReqVo reqVo) throws Exception {
		
		boardMapper.insertComment(user, reqVo);
		
		final CommentItem result = boardMapper.selectCommentByNo(reqVo.getCommentNo());
		
		result.setEmotionList( BoardDaoImpl.normalizeEmotionList(
				result.getEmotionList(),
				reqVo.getEmojiSetType()
		) );
		
		return result;
	}
}
