package com.utime.memoBom.board.service.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.utime.memoBom.board.dao.BoardDao;
import com.utime.memoBom.board.dao.ShareDao;
import com.utime.memoBom.board.dao.TopicDao;
import com.utime.memoBom.board.dto.ShareDto;
import com.utime.memoBom.board.service.ShareService;
import com.utime.memoBom.board.vo.EShareTargetType;
import com.utime.memoBom.board.vo.FragmentItem;
import com.utime.memoBom.board.vo.ShareVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.ReturnBasic;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class ShareServiceImpl implements ShareService {

	final ShareDao shareDao;

	final TopicDao topicDao;
	
	final BoardDao boardDao;

	@Value("${appName}")
	private String appName;

	@Value("${env.imagePath}")
	private String ValueEnvImagePath;

	private TwemojiFetcher fetcher;

	@PostConstruct
	private void init() {

		final File imogiPathFile = new File(ValueEnvImagePath, "imogi");

		// 저장 디렉토리 생성
		if (!imogiPathFile.exists()) {
			imogiPathFile.mkdirs();
		}

		Path imogiPath = imogiPathFile.toPath();
		log.info(imogiPath.toString());

		try {
			fetcher = new TwemojiFetcher(imogiPath);
		} catch (IOException e) {
			log.error("", e);
		}
	}

	@Override
	public ShareDto loadShareInfo(LoginUser user, String uid, boolean isBot) {

		final ShareVo vo = shareDao.loadShareInfo(user, uid, isBot);

		final ShareDto result = new ShareDto();
		result.setTitle(appName);

		if (vo == null) {
			result.setText("공유 주소를 확인하세요.");
			return result;
		}

		result.setText(vo.getText());

		final EShareTargetType targetType = vo.getTargetType();

		final String fullUrl;

		if (targetType == EShareTargetType.Topic) {
			fullUrl = "/Mosaic/index.html?uid=" + vo.getUid();
			result.setImage( "/Share/Mosaic/" + vo.getUid() + ".png" );
		} else if (targetType == EShareTargetType.Fragment) {
			fullUrl = "/Fragment/index.html?fragUid=" + vo.getUid();
			result.setImage( "/Share/Fragment/" + vo.getUid() + ".webp" );
		} else {
			fullUrl = "";
		}

		result.setUrl(fullUrl);

		return result;
	}

	@Override
	public ReturnBasic makeShareInfo(HttpServletRequest request, LoginUser user, EShareTargetType targetType,
			String targetUid) {

		final ReturnBasic result = new ReturnBasic();

		ShareVo vo;
		try {
			vo = shareDao.addShareInfo(user, targetType, targetUid);
		} catch (Exception e) {
			log.error("", e);
			return result.setCodeMessage("E", "공유 대상 조회에 실패했습니다.");
		}

		if (vo == null) {
			return result.setCodeMessage("E", "공유 대상 정보가 없습니다.");
		}

		final ShareDto shareDto = new ShareDto();

		shareDto.setTitle(appName);
		shareDto.setText(vo.getText());

		final String fullUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath()
				+ "/Share/" + vo.getUid() + ".html";
		shareDto.setUrl(fullUrl);

		result.setData(shareDto);

		return result;
	}

	private byte[] makeOgPngBytes(Path emojiPng, String hexBg) {

		try {
			final BufferedImage em = ImageIO.read(emojiPng.toFile());
			if (em == null)
				return null;

			final int W = 800, H = 400;
			BufferedImage out = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = out.createGraphics();
			try {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

				// 배경
				Color bg = parseHexColor(hexBg);
				g.setColor(bg);
				g.fillRect(0, 0, W, H);

				// (선택) 카드 느낌
				int pad = 28;
				Shape card = new RoundRectangle2D.Float(pad, pad, W - pad * 2, H - pad * 2, 48, 48);
				g.setColor(new Color(255, 255, 255, 70));
				g.fill(card);

				// 이모지 크기(권장: 180~260px 사이)
				int target = 220;
				int x = (W - target) / 2;
				int y = (H - target) / 2;

				g.drawImage(em, x, y, target, target, null);

			} finally {
				g.dispose();
			}

			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				ImageIO.write(out, "png", baos);
				return baos.toByteArray();
			}

		} catch (Exception e) {
			return null;
		}
	}

	private Color parseHexColor(String hex) {
		String s = hex.trim();
		if (s.startsWith("#"))
			s = s.substring(1);
		if (s.length() != 6)
			throw new IllegalArgumentException("hex color must be 6 digits: " + hex);

		int r = Integer.parseInt(s.substring(0, 2), 16);
		int g = Integer.parseInt(s.substring(2, 4), 16);
		int b = Integer.parseInt(s.substring(4, 6), 16);
		return new Color(r, g, b);
	}

	@Override
	public byte[] drawTopicOgImagePngBytes(String uid) {

		final TopicVo vo = topicDao.loadTopic(uid);
		if (vo == null) {
			return null;
		}

		String emoji = vo.getImogi(), hexBg = vo.getColor();

		final Path emojiPng = fetcher.getOrDownloadPng72(emoji);

		if (emojiPng == null) {
			log.warn("getOrDownloadPng72 {} is null", emoji);
			return null;
		}

		return this.makeOgPngBytes(emojiPng, hexBg);
	}
	
	@Override
	public byte[] drawFragmentOgImagePngBytes(String uid) {
		
		final FragmentItem vo = boardDao.loadFragment(null, uid);
		if( vo == null ) {
			return null;
		}
		
		return null;
	}

}
