package com.utime.memoBom.board.dao.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.utime.memoBom.board.dao.BoardDao;
import com.utime.memoBom.board.dto.BoardReqDto;
import com.utime.memoBom.board.dto.EmotionDto;
import com.utime.memoBom.board.mapper.BoardMapper;
import com.utime.memoBom.board.mapper.TopicMapper;
import com.utime.memoBom.board.vo.CommentItem;
import com.utime.memoBom.board.vo.CommentReqVo;
import com.utime.memoBom.board.vo.EEmotionCode;
import com.utime.memoBom.board.vo.EmojiSetType;
import com.utime.memoBom.board.vo.EmojiSets;
import com.utime.memoBom.board.vo.EmotionItem;
import com.utime.memoBom.board.vo.FragmentImageVo;
import com.utime.memoBom.board.vo.FragmentItem;
import com.utime.memoBom.board.vo.FragmentListReqVO;
import com.utime.memoBom.board.vo.FragmentVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.board.vo.query.MyCommentVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.UserDevice;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class BoardDaoImpl implements BoardDao {

	final BoardMapper boardMapper;
	final TopicMapper topicMapper;
	
	@Value("${env.imagePath}")
	private String ValueEnvImagePath;
	
	private String ValueImagePath, ValueThumbPath;
	
	@PostConstruct
	private void init() {
		ValueImagePath = ValueEnvImagePath + "/image";
		ValueThumbPath = ValueEnvImagePath + "/thumb";
	}

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
	
	
	
	@Transactional(rollbackFor = Exception.class)
	private int upsertImage( LoginUser user, long fragmentNo, BoardReqDto reqVo) {
		int result = 0;
		
		// 이미지
		final MultipartFile image = reqVo.getImage(), thumb = reqVo.getThumb();
		
		if( image == null || image.isEmpty() || thumb == null || thumb.isEmpty() )
			return result;
		
		// 1. 년/월 경로 생성 (예: 2026/02)
		final String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy" + File.separator + "MM"));
		
		final File imagePath = new File(ValueImagePath, datePath), thumbPath = new File(ValueThumbPath, datePath);;
		
		// 저장 디렉토리 생성
        if (!imagePath.exists()) {
        	imagePath.mkdirs();
        }
        if (!thumbPath.exists()) {
        	thumbPath.mkdirs();
        }
		
		final String fileName = UUID.randomUUID().toString() + ".webp";
		final File imageFile = new File( imagePath, fileName);
		final File thumbFile = new File( thumbPath, fileName );
		final long imageSize = image.getSize();
		
		try {
            // 파일 저장
            image.transferTo(imageFile);
            thumb.transferTo(thumbFile);
            
            final String storageName = imageFile.getCanonicalPath().substring( ValueImagePath.length() );
            
            final FragmentImageVo imgVo = new FragmentImageVo(
            		0, 
            		storageName,
            		reqVo.getImageName(), 
            		reqVo.getImageWidth(), 
            		reqVo.getImageHeight(), 
            		imageSize);
            
            result = boardMapper.upsertFragmentImage(user, fragmentNo, imgVo);
            
        } catch (IOException e) {
            result = -1;
        }
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveFragment(LoginUser user, UserDevice device, BoardReqDto reqVo) throws Exception {

		final FragmentVo item = new FragmentVo();
		item.setContent(reqVo.getContent());
		item.setIp(reqVo.getIp());
		
		int result = 0;
		if( AppUtils.isNotEmpty(reqVo.getUid()) ) {
			if( reqVo.isDeleted() ) {
				result += boardMapper.removeFragment( user.userNo(), reqVo.getUid() );
			}else {
				item.setUid(reqVo.getUid());
				result += boardMapper.updateFragment( user.userNo(), item );
				
				final FragmentItem f = boardMapper.getFragment( 0, reqVo.getUid() );
				result += this.upsertImage(user, f.getFragmentNo(), reqVo);
			}
			
			return result;
		}

		final TopicVo topic = topicMapper.loadTopic(reqVo.getTopicUid(), -1L);
		if (topic == null) {
			throw new Exception("topic is null.");
		}

		result += boardMapper.insertFragment(user, device, topic, item);
		result += topicMapper.increaseTopicStatsFragmentCount( topic.getTopicNo() );
		result += this.upsertImage(user, item.getFragmentNo(), reqVo);

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
	public List<FragmentItem> loadFragmentList(LoginUser user, FragmentListReqVO reqVo) {
		
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
	public FragmentItem loadFragment(LoginUser user, String fUid) {
		final FragmentListReqVO reqVo = new FragmentListReqVO();
		reqVo.setFragUid(fUid);
		
		final List<FragmentItem> list = boardMapper.loadFragmentList(user, reqVo);
		
		return ( AppUtils.isNotEmpty(list))? list.get(0):null;
	}

	@Override
	public List<CommentItem> loadCommentsList(LoginUser user, String uid, int pageNo, EmojiSetType emojiSetType) {
		
		final List<CommentItem> result = boardMapper.loadCommentsList(uid, pageNo);
		
		if( AppUtils.isNotEmpty(result)) {
			
			result.forEach( comment -> {
				comment.setEmotionList( BoardDaoImpl.normalizeEmotionList(
				        comment.getEmotionList(),
				        emojiSetType
				) );
			});
		}
		return result;
	}
	
	/**
	 * @return true면 스크랩, false면 스크랩 취소
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean procScrap(LoginUser user, String fragmentUid) throws Exception {
		
		final boolean exists = boardMapper.existsScrap(user.userNo(), fragmentUid);
	    if (exists) {
	    	boardMapper.deleteScrap(user.userNo(), fragmentUid);
	    } else {
	    	boardMapper.insertScrap(user.userNo(), fragmentUid);
	    }
		return !exists;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<EmotionItem> procEmotion(LoginUser user, EmotionDto emotionReqVo) throws Exception {

		int deleted = boardMapper.deleteEmotion(user.userNo(), emotionReqVo);
		if (deleted == 0) {
			deleted += boardMapper.upsertEmotion(user.userNo(), emotionReqVo);
		}
		
		return BoardDaoImpl.normalizeEmotionList(
				boardMapper.selectEmotionList(emotionReqVo),
				emotionReqVo.getEmojiSetType()
			);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public CommentItem saveComment(LoginUser user, CommentReqVo reqVo) throws Exception {
		
		boardMapper.insertComment(user, reqVo);
		
		topicMapper.increaseTopicStatsCommentCount( reqVo.getUid() );
		
		final CommentItem result = boardMapper.selectCommentByNo(reqVo.getCommentNo());
		
		result.setEmotionList( BoardDaoImpl.normalizeEmotionList(
				result.getEmotionList(),
				reqVo.getEmojiSetType()
		) );
		
		return result;
	}
	
	@Override
	public List<FragmentItem> listMyFragments(LoginUser user, String keyword, int pageNo) {
		
		return boardMapper.listMyFragments(user, keyword, pageNo);
	}
	
	@Override
	public List<MyCommentVo> listMyComments(LoginUser user, String keyword, int pageNo) {
		
		return boardMapper.listMyComments(user, keyword, pageNo);
	}
	
	@Override
	public List<FragmentItem> listMyScrapFragments(LoginUser user, String keyword, int pageNo) {
		
		return boardMapper.listMyScrapFragments(user, keyword, pageNo);
	}
}
