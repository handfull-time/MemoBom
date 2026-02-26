package com.utime.memoBom.board.controller;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.Strictness;
import com.utime.memoBom.board.dto.BoardMainParamDto;
import com.utime.memoBom.board.dto.BoardReqDto;
import com.utime.memoBom.board.dto.EmotionDto;
import com.utime.memoBom.board.dto.FragmentDto;
import com.utime.memoBom.board.dto.FragmentListDto;
import com.utime.memoBom.board.service.BoardService;
import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.board.vo.CommentReqVo;
import com.utime.memoBom.board.vo.EEmotionTargetType;
import com.utime.memoBom.board.vo.EmojiSetType;
import com.utime.memoBom.board.vo.FragmentListReqVO;
import com.utime.memoBom.board.vo.query.TopicResultVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.BinResultVo;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// Fragment(편린) 로 바꾸자.
@Controller
@RequestMapping("Fragment")
@RequiredArgsConstructor
public class BoardController {
	
	final BoardService boardServce;
	final TopicService topicServce;
	final UserService userServce;
	
	final String KeyTopics = "topics";
	
	/**
	 * 메인 화면
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(path = {"", "/", "index.html" })
    public String boardMain( ModelMap model, BoardMainParamDto param) {
		model.addAttribute("param", param);
		return "Board/BoardMain";
    }

	/**
	 * 글 수정하기
	 * @param request
	 * @param model
	 * @param user
	 * @param uid
	 * @return
	 */
	@GetMapping(value = "Tessera.html", params = "uid")
	public String modifyFragment(HttpServletRequest request, ModelMap model, LoginUser user, 
	        @RequestParam(required = true) String uid) {
		
		final String key = boardServce.createKey(request, user);
		if( key == null ) {
			model.addAttribute("res", new ReturnBasic("E", "접속하신 장치를 확인해 주세요.") );
			model.addAttribute(AppDefine.KeyShowFooter, false );
		    model.addAttribute(AppDefine.KeyLoadScript, false );
			return "Common/ErrorAlert";
		}
		
		final FragmentDto resultItem = boardServce.loadFragment(user, uid);
		if( resultItem == null ) {
			model.addAttribute("res", new ReturnBasic("E", "요청 값이 유효하지 않습니다.") );
			model.addAttribute(AppDefine.KeyShowFooter, false );
		    model.addAttribute(AppDefine.KeyLoadScript, false );
			return "Common/ErrorAlert";
		}
		
		final ReturnBasic topicRes = topicServce.loadTopic(user, resultItem.getTopic().getUid());
		if( topicRes.isError() ) {
			model.addAttribute("res", topicRes );
			model.addAttribute(AppDefine.KeyShowFooter, false );
		    model.addAttribute(AppDefine.KeyLoadScript, false );
			return "Common/ErrorAlert";
		}
		
		model.addAttribute(KeyTopics, List.of( topicRes.getData() ) );
		model.addAttribute("item", resultItem );
		model.addAttribute("itemImageUrl", resultItem.getImage() == null
				? null
				: "/Fragment/Image/" + resultItem.getImage().getUid());
		model.addAttribute("seal", key );
		
		final Gson gson = new GsonBuilder()
				.setStrictness(Strictness.LENIENT)
				.setPrettyPrinting()
	            .create();
		
		System.out.println( gson.toJson(resultItem) );
		
		return "Board/BoardWrite";
	}
	
	/**
	 * 새 글 쓰기
	 * @param request
	 * @param model
	 * @param user
	 * @param topicUid
	 * @return
	 */
	@GetMapping(value = "Tessera.html", params = "!uid")
	public String newFragment( HttpServletRequest request, ModelMap model, LoginUser user, 
			@RequestParam(name="topic", required = false) String topicUid ) {

		final List<TopicResultVo> list;
		if( ! AppUtils.isEmpty(topicUid) ) {
			final ReturnBasic topicRes = topicServce.loadTopic( user, topicUid );
			if( topicRes.isError() ) {
				model.addAttribute("res", topicRes );
				model.addAttribute(AppDefine.KeyShowFooter, false );
			    model.addAttribute(AppDefine.KeyLoadScript, false );
				return "Common/ErrorAlert";
			}
			
			final TopicResultVo topic = (TopicResultVo)topicRes.getData();
			list = List.of(topic);
		}else {
			list = topicServce.loadUserTopicList( user );
			
			if( AppUtils.isEmpty(list)) {
				model.addAttribute("res", new ReturnBasic("E", "개인 주제를 먼저 정하세요.") );
				model.addAttribute(AppDefine.KeyShowFooter, false );
			    model.addAttribute(AppDefine.KeyLoadScript, false );
				return "Common/ErrorAlert";
			}
		}
		
		final String key = boardServce.createKey(request, user);
		if( key == null ) {
			model.addAttribute("res", new ReturnBasic("E", "접속하신 장치를 확인해 주세요.") );
			model.addAttribute(AppDefine.KeyShowFooter, false );
		    model.addAttribute(AppDefine.KeyLoadScript, false );
			return "Common/ErrorAlert";
		}
		
		model.addAttribute(KeyTopics, list );
		model.addAttribute("item", null );
		model.addAttribute("itemImageUrl", null);
		model.addAttribute("seal", key );
		
		return "Board/BoardWrite";
	}
	
	/**
	 * 편린 저장
	 * @param request
	 * @param user
	 * @param device
	 * @param reqVo
	 * @return
	 */
	@ResponseBody
	@PostMapping("Save.json")
	public ReturnBasic saveFragment( HttpServletRequest request, LoginUser user, UserDevice device, BoardReqDto reqVo ) {
		
		reqVo.setIp(AppUtils.getRemoteAddress(request));
		
		final ReturnBasic result = boardServce.saveFragment( user, device, reqVo );
		
		return result;
	}

	/**
	 * 편린 목록 조회
	 * @param user
	 * @param reqVo
	 * @return
	 */
	@ResponseBody
	@GetMapping(path = "Fragments.json")
    public FragmentListDto loadFragmentList( HttpServletRequest request, LoginUser user, FragmentListReqVO reqVo ) {

		return boardServce.loadFragmentList( request, user, reqVo );
    }
	
	/**
	 * 댓글 목록 조회
	 * @param user
	 * @param uid
	 * @param pageNo
	 * @return
	 */
	@ResponseBody
	@PostMapping(path = "SaveComment.json")
    public ReturnBasic saveComment( HttpServletRequest request, LoginUser user, UserDevice device, @RequestBody CommentReqVo reqVo ) {

		reqVo.setIp(AppUtils.getRemoteAddress(request));
		reqVo.setDevice( device.getDevice().name() );
		
		return boardServce.saveComment( user, reqVo );
    }
	
	/**
	 * 댓글 목록 조회
	 * @param user
	 * @param uid
	 * @param pageNo
	 * @return
	 */
	@ResponseBody
	@GetMapping(path = "Comments.json")
    public ReturnBasic loadCommentsList( LoginUser user, 
    		@RequestParam() String uid, 
    		@RequestParam(defaultValue = "1") int pageNo,
    		@RequestParam() EmojiSetType emojiSetType) {

		return boardServce.loadCommentsList( user, uid, pageNo, emojiSetType );
    }
	
	/**
	 * 스크랩 처리
	 * @param user
	 * @param reqVo
	 * @return
	 */
	@ResponseBody
	@PostMapping(path = "Scrap.json")
    public ReturnBasic procScrap( LoginUser user, @RequestBody EmotionDto reqVo ) {

		return boardServce.procScrap( user, reqVo.getUid() );
    }
	
	/**
	 * 편린 감정표현 처리
	 * @param user
	 * @param reqVo
	 * @return
	 */
	@ResponseBody
	@PostMapping(path = "EmotionFragment.json")
    public ReturnBasic procEmotionFragment( LoginUser user, @RequestBody EmotionDto reqVo ) {

		reqVo.setTargetType(EEmotionTargetType.Board);
		return boardServce.procEmotion( user, reqVo );
    }
	
	/**
	 * 댓글 감정표현 처리
	 * @param user
	 * @param reqVo
	 * @return
	 */
	@ResponseBody
	@PostMapping(path = "EmotionComment.json")
    public ReturnBasic procEmotionComment( LoginUser user, @RequestBody EmotionDto reqVo ) {

		reqVo.setTargetType(EEmotionTargetType.Comment);
		return boardServce.procEmotion( user, reqVo );
    }
	
	/**
	 * Fragment 이미지 정보 갖고 오기
	 * @param isThumb
	 * @param index
	 * @param uid
	 * @param response
	 * @throws Exception
	 */
	private void getFragmentImageData(boolean isThumb, String uid,
	                                HttpServletResponse response) throws Exception {

	    final BinResultVo image = boardServce.getImage(isThumb, uid);

	    if (image == null || image.getBinary() == null) {
	        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	        return;
	    }

	    String mimeType = image.getMimeType();
	    if (mimeType == null || mimeType.isBlank()) {
	        mimeType = "image/webp";
	    }

	    response.setContentType(mimeType);
	    response.setContentLength(image.getBinary().length);
	    
	    // 한글 파일명 인코딩 (RFC 5987 준수)
	    final String fileName = (image.getName() != null) ? image.getName() : "image";
	    final String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

	    // Content-Disposition 헤더 설정 (inline: 브라우저 출력, attachment: 다운로드)
	    // filename*: UTF-8 인코딩을 명시적으로 선언하여 한글 깨짐 방지
	    response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"; filename*=UTF-8''" + encodedFileName);
	    
	    //브라우저 캐시도 허용
	    response.setHeader("Cache-Control", "public, max-age=300");

	    try (OutputStream os = response.getOutputStream()) {
	        os.write(image.getBinary());
	        os.flush();
	    }
	}
	
	/**
	 * fragment의 섬네일 이미지
	 * @param uid 이미지의 uid
	 * @param response
	 * @throws Exception
	 */
	@GetMapping("Thumb/{uid}")
	public void getFragmentImageThumb(@PathVariable String uid,
	                                HttpServletResponse response) throws Exception {
	    this.getFragmentImageData(true, uid, response);
	}
	
	/**
	 * fragment의 이미지
	 * @param uid 이미지의 uid
	 * @param response
	 * @throws Exception
	 */
	@GetMapping("Image/{uid}")
	public void getFragmentImage(@PathVariable String uid,
	                                HttpServletResponse response) throws Exception {
	    this.getFragmentImageData(false, uid, response);
	}
}
