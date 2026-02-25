package com.utime.memoBom.board.vo;

public record FragmentImageVo(
	int imageOrder,
	String storageName,
	String originalName,
	int width, int height,
	long fileSize
) {}
