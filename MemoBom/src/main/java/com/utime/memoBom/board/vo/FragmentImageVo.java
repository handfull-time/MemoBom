package com.utime.memoBom.board.vo;

public record FragmentImageVo(
	String uid,
	int imageOrder,
	String storageName,
	String originalName,
	String mimeType,
	int width, int height,
	long fileSize
) {}
