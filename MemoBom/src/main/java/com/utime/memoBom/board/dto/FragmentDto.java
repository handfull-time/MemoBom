package com.utime.memoBom.board.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.utime.memoBom.board.vo.EmotionItem;
import com.utime.memoBom.board.vo.FragmentItem;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.user.dto.UserDto;
import com.utime.memoBom.user.vo.UserVo;

import lombok.Data;

@Data
public class FragmentDto {
	/** 게시글 uid */
	String uid;
	/** topic 정보 */
	TopicDto topic;
	/** 사용자 정보 */
	UserDto user;
	/** 생성일 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	Date regDate;
	/** 내용 */
	String content;
	/** 스크랩 여부 */
	boolean scrap;
	/** 이모션 목록 */
	List<EmotionItem> emotionList;
	/** 해시 태그 목록 */
	List<String> hashtagList;
	
	public static FragmentDto of(FragmentItem item) {
		
		if( item == null ) {
			return null;
		}
		
		final FragmentDto result = new FragmentDto();
		
		result.uid = item.getUid();
		result.regDate = item.getRegDate();
		result.content = item.getContent();
		result.scrap = item.isScrap();
		result.emotionList = item.getEmotionList() == null
		        ? List.of() : List.copyOf(item.getEmotionList());
		result.hashtagList = item.getHashtagList() == null
		        ? List.of() : List.copyOf(item.getHashtagList());
		
		final UserVo user = item.getUser();
		if( user != null ){
			result.user = new UserDto(user.getUid(), user.getNickname(), user.getProfileUrl(), user.getFontSize());
		}
		
		final TopicVo topic = item.getTopic();
		if( topic != null ) {
			result.topic = TopicDto.of(topic);
		}
		
		return result;
	}
}
