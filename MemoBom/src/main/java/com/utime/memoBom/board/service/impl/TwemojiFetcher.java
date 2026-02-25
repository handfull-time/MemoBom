package com.utime.memoBom.board.service.impl;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 이미지 웹에서 다운받아 처리 함.
 */
public final class TwemojiFetcher {

	// jsDelivr (twitter/twemoji) - 72x72 PNG
	private static final String TWEMOJI_BASE_72 = "https://cdn.jsdelivr.net/gh/twitter/twemoji@latest/assets/72x72/";

	private final HttpClient http;
	private final Path cacheDir;

	public TwemojiFetcher(Path cacheDir) throws IOException {
		this.http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
		this.cacheDir = cacheDir;
		Files.createDirectories(cacheDir);
	}

	/** 성공 시 로컬 캐시 파일 경로 반환, 실패 시 null */
	public Path getOrDownloadPng72(String emoji) {
		
		final String cps = toTwemojiCodepoints(emoji);
		if (cps == null)
			return null;

		final Path target = cacheDir.resolve(cps + ".png");
		if (Files.isRegularFile(target))
			return target;

		final String url = TWEMOJI_BASE_72 + cps + ".png";
		try {
			
			final HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).header("User-Agent", "OgImageBot/1.0").GET().build();

			final HttpResponse<Path> res = http.send(req, HttpResponse.BodyHandlers.ofFile(
					// 임시파일에 받고, 성공 시 rename
					cacheDir.resolve(cps + ".tmp")));

			if (res.statusCode() != 200) {
				safeDelete(cacheDir.resolve(cps + ".tmp"));
				return null;
			}

			final Path tmp = res.body();
			Files.move(tmp, target, REPLACE_EXISTING);
			return target;

		} catch (Exception e) {
			safeDelete(cacheDir.resolve(cps + ".tmp"));
			return null;
		}
	}

	private static void safeDelete(Path p) {
		try {
			Files.deleteIfExists(p);
		} catch (Exception ignored) {
		}
	}

	private static String toTwemojiCodepoints(String emoji) {
		
		if (emoji == null || emoji.isBlank())
			return null;

		List<String> parts = new ArrayList<>();
		emoji.codePoints().forEach(cp -> {
			// Variation Selector-16 (emoji presentation) 제거
			if (cp == 0xFE0F)
				return;
			parts.add(Integer.toHexString(cp));
		});

		if (parts.isEmpty())
			return null;
		return String.join("-", parts).toLowerCase();
	}

//    private static String toTwemojiPngUrl72(String emoji) {
//        String cps = toTwemojiCodepoints(emoji);
//        if (cps == null) return null;
//        return TWEMOJI_BASE_72 + cps + ".png";
//    }
}