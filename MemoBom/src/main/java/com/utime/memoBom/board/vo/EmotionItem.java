package com.utime.memoBom.board.vo;

import lombok.Data;

@Data
public class EmotionItem {

	EEmotionCode emotion;
	int count;
	
	public EmotionItem() {
		this(null, 0);
	}
	
	public EmotionItem(EEmotionCode e, int i) {
		this.emotion = e;
		this.count = i;
	}
}
