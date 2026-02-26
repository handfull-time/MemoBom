package com.utime.memoBom.board.dto;

import com.utime.memoBom.board.vo.query.BoardImageVo;

import lombok.Data;

/**
 * 이미지 정보
 */
@Data
public class ImageDto {
	/** 이미지 고유 ID */
	String uid;
	/** 가로 세로 크기 */
	int width, height;
	/** 원래 파일 이름 */
	String name;
	
	public static ImageDto of(BoardImageVo item ) {
		
		if( item == null || item.getUid() == null || item.getUid().isEmpty() ) {
			return null;
		}
		
		final ImageDto result = new ImageDto();
		
		result.uid = item.getUid();
		result.width = item.getWidth();
		result.height = item.getHeight();
		result.name = item.getOriginName();
		
		return result;
	}
}
