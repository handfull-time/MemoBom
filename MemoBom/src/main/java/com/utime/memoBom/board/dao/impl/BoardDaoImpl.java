package com.utime.memoBom.board.dao.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.memoBom.board.dao.BoardDao;
import com.utime.memoBom.board.mapper.BoardMapper;
import com.utime.memoBom.board.mapper.TopicMapper;
import com.utime.memoBom.board.vo.BoardReqVo;
import com.utime.memoBom.board.vo.FragmentVo;
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

		final Set<String> hashTags = parseTags(reqVo.getHashTag());
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
}
