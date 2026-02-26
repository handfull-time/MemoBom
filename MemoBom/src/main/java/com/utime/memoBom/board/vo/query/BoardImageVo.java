package com.utime.memoBom.board.vo.query;

import lombok.Data;

@Data
public class BoardImageVo {
	/** uid */
	String uid;
	/** 가로 */
	int width;
	/** 세로 */
	int hieght;
	/** 이름 */
	String originName;
	/** 파일 저장소 이름 */
	String storageName;
	/** 파일 속성 */
	String mimeType;
}
