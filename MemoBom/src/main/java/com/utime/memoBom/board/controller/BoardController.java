package com.utime.memoBom.board.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.board.service.BoardService;
import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.board.vo.BoardReqVo;
import com.utime.memoBom.board.vo.CommentReqVo;
import com.utime.memoBom.board.vo.EEmotionTargetType;
import com.utime.memoBom.board.vo.EmojiSetType;
import com.utime.memoBom.board.vo.EmotionReqVo;
import com.utime.memoBom.board.vo.FragmentListReqVO;
import com.utime.memoBom.board.vo.FragmentVo;
import com.utime.memoBom.board.vo.ShareVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.service.UserService;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

// Fragment(편린) 로 바꾸자.
@Controller
@RequestMapping("Fragment")
@RequiredArgsConstructor
public class BoardController {
	
	final BoardService boardServce;
	final TopicService topicServce;
	final UserService userServce;
	
	final String KeyUser = "accessUser";
	final String KeyTopic = "topic";
	final String KeyTopics = "topics";
	
	/**
	 * 로그인 화면
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(path = {"", "/", "index.html" })
    public String boardMain( ModelMap model, UserVo user ) {
		
		if( user == null ) {
			return "redirect:/Auth/Login.html";
		}else if( ! topicServce.hasTopic(user) ){
			return "redirect:/Mosaic/index.html";
		}else {
			
			model.addAttribute(this.KeyUser, user.getUid());
			model.addAttribute(this.KeyTopic, null);
			
			return "Board/BoardMain";
		}
    }

	/**
	 * 새 글 쓰기
	 * @param request
	 * @param model
	 * @param user
	 * @param topicUid
	 * @return
	 */
	@GetMapping("Tessera.html")
	public String newFragment( HttpServletRequest request, ModelMap model, UserVo user, 
			@RequestParam(name="topic", required = false) String topicUid ) {

		final List<TopicVo> list;
		if( ! AppUtils.isEmpty(topicUid) ) {
			final TopicVo topic = topicServce.loadTopic(topicUid);
			if( topic == null ) {
				model.addAttribute("res", new ReturnBasic("E", "존재하지 않는 주제입니다.") );
				model.addAttribute(AppDefine.KeyShowFooter, false );
			    model.addAttribute(AppDefine.KeyLoadScript, false );
				return "Common/ErrorAlert";
			}
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
	public ReturnBasic saveFragment( HttpServletRequest request, UserVo user, UserDevice device, @RequestBody BoardReqVo reqVo ) {
		
		reqVo.setIp(AppUtils.getRemoteAddress(request));
		
		return boardServce.saveFragment( user, device, reqVo );
	}
	
	/**
	 * 특정 주제의 게시글 보기
	 * @param model
	 * @param user 현재 로그인한 사용자
	 * @param topicUid 특정 주제의 글만 보기 (선택)
	 * @param userUid 특정 사용자의 글만 보기 (선택)
	 * @return
	 */
	@GetMapping(path = "Mosaic.html")
    public String boardTopicFromUid( ModelMap model, UserVo user, 
    		@RequestParam(name="uid", required = false) String topicUid,
    		@RequestParam(name="user", required = false) String userUid,
    		@RequestParam(name="comment", required = false) String commentUid) {

//		if( AppUtils.isNotEmpty(commentUid) ) {
//			model.addAttribute("commentUid", commentUid);
//		}else {
//			model.addAttribute(KeyUser, userServce.getUserFromUid(userUid));
//			model.addAttribute(KeyTopic, topicServce.loadTopic(topicUid) );
//		}
		model.addAttribute(KeyTopic, topicUid);
		model.addAttribute(KeyUser, userUid);
		model.addAttribute("commentUid", commentUid);

		return "Board/BoardMain";
    }
	
	/**
	 * 편린 목록 조회
	 * @param user
	 * @param reqVo
	 * @return
	 */
	@ResponseBody
	@GetMapping(path = "Fragments.json")
    public ReturnBasic loadFragmentList( UserVo user, FragmentListReqVO reqVo ) {

		return boardServce.loadFragmentList( user, reqVo );
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
    public ReturnBasic saveComment( HttpServletRequest request, UserVo user, UserDevice device, @RequestBody CommentReqVo reqVo ) {

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
    public ReturnBasic loadCommentsList( UserVo user, 
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
    public ReturnBasic procScrap( UserVo user, @RequestBody EmotionReqVo reqVo ) {

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
    public ReturnBasic procEmotionFragment( UserVo user, @RequestBody EmotionReqVo reqVo ) {

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
    public ReturnBasic procEmotionComment( UserVo user, @RequestBody EmotionReqVo reqVo ) {

		reqVo.setTargetType(EEmotionTargetType.Comment);
		return boardServce.procEmotion( user, reqVo );
    }
	/**
	 * 공유 정보 생성
	 * @param user
	 * @param uid
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@GetMapping("Share.json")
	public ReturnBasic getShareInfo(HttpServletRequest request, UserVo user, @RequestParam() String uid) throws Exception {
		
		final ShareVo share = boardServce.loadShareInfo(user, uid);

		final String fullUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath() + "/Share/" + uid;
		share.setUrl(fullUrl);
		
		final ReturnBasic result = new ReturnBasic();
		result.setData(share);
		return result;
	}
}

