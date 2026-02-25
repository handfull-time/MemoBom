package com.utime.memoBom.board.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.board.dto.ShareDto;
import com.utime.memoBom.board.service.ShareService;
import com.utime.memoBom.board.vo.EShareTargetType;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.ReturnBasic;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 공유하기
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ShareController {

	final ShareService shareServce;
	
	@Value("${appName}")
	private String appName;
	
	@GetMapping("Share/{uid}.html")
	public String getShareInfoView(HttpServletRequest request, ModelMap model, LoginUser user, @PathVariable() String uid) {
		
    	final String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
    	final boolean isBot = AppUtils.isBot(userAgent);
    	log.info("Bot 진입("+isBot+") : " + userAgent);
    	
    	final ShareDto vo = shareServce.loadShareInfo( user, uid, isBot);
    	
    	if( isBot ) {
    	
    		final String baseUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath();
    		vo.setUrl( baseUrl + vo.getUrl() );
    		vo.setImage( baseUrl + vo.getImage() );
    		model.addAttribute("item", vo);
    		
    		return "Share/ShowShareBot";
    	}
	    		
		return "redirect:" + vo.getUrl();
	}
	
	/**
	 * Topic 공유 정보 생성
	 * @param request
	 * @param user
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@GetMapping("TopicShare.json")
	public ReturnBasic getTopicShareInfo(HttpServletRequest request, LoginUser user, @RequestParam() String uid) throws Exception {
		
		return shareServce.makeShareInfo(request, user, EShareTargetType.Topic, uid);
	}
	
	/**
	 * Fragment 공유 정보 생성
	 * @param user
	 * @param uid
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@GetMapping("FragmentShare.json")
	public ReturnBasic getShareInfo(HttpServletRequest request, LoginUser user, @RequestParam() String uid) throws Exception {
		
		return shareServce.makeShareInfo(request, user, EShareTargetType.Fragment, uid);
	}
	
	/**
	 * Topic og용 이미지 보기
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@GetMapping(value = "Share/Mosaic/{uid}.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> ogMosaic( @PathVariable String uid ) throws Exception {
    	
        final byte[] png = shareServce.drawTopicOgImagePngBytes(uid);
        if (png == null || png.length == 0) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .contentLength(png.length)
                .body(png);
    }
    
    private static final MediaType IMAGE_WEBP = MediaType.parseMediaType("image/webp");
    
    @ResponseBody
    @GetMapping(value = "Share/Fragment/{uid}.webp", produces = "image/webp")
    public ResponseEntity<byte[]> ogFragment(@PathVariable String uid) throws Exception {

        final byte[] webp = shareServce.drawFragmentOgImagePngBytes(uid);

        // 1️⃣ 정상 WebP 반환
        if (webp != null && webp.length > 0) {
            return ResponseEntity.ok()
                    .contentType(IMAGE_WEBP)
                    .contentLength(webp.length)
                    .body(webp);
        }

        // 2️⃣ fallback: favicon PNG 반환
        final ClassPathResource resource = new ClassPathResource("static/images/favicon/favicon_512.png");

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        final byte[] fallback = resource.getInputStream().readAllBytes();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .contentLength(fallback.length)
                .body(fallback);
    }
	
}
