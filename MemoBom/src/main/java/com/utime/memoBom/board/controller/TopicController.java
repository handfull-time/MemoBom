package com.utime.memoBom.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.board.dto.InviteUserDto;
import com.utime.memoBom.board.dto.InviteDecisionDto;
import com.utime.memoBom.board.dto.TopicDto;
import com.utime.memoBom.board.dto.TopicSaveDto;
import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.board.vo.ETopicSortType;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.board.vo.query.TopicResultVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.ReturnBasic;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("Topic")
@RequiredArgsConstructor
public class TopicController {
	
	// Topic 서비스
	final TopicService topicServce;
	
	/**
	 * 로그인 화면
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(path = {"", "/", "index.html" })
    public String topicMain( ModelMap model, LoginUser user, 
    		@RequestParam(required = false) String keyword,
    		@RequestParam(required = false) String uid) {
		
		if(topicServce.isEmpty() && user != null ) {
			return "redirect:/Topic/Ensemble.html";
		}else {
			model.addAttribute("keyword", keyword);
			model.addAttribute("uid", uid);
			return "Topic/TopicMain";
		}
    }
	
	/**
	 * 동일 이름 검사
	 * @param name
	 * @return
	 */
	@ResponseBody
	@GetMapping("SameName.json")
	public ReturnBasic checkSameName( TopicDto reqVo ) {
		
		return topicServce.checkSameName( reqVo.getUid(), reqVo.getName() );
	}
	
	/**
	 * 얘깃거리 상세 보기
	 * @param reqVo
	 * @return
	 */
	@GetMapping("Ensemble.html")
	public String topicNew( HttpServletRequest request, ModelMap model, LoginUser user ) {
		
		model.addAttribute("topic", new TopicVo());
		model.addAttribute(KeySeal, topicServce.createKey(request, user));
		
		return "Topic/TopicItem";
	}
	
	final String KeySeal = "seal";
	
	/**
	 * 얘깃거리 상세 보기
	 * @param reqVo
	 * @return
	 */
	@GetMapping("Item.html")
	public String topicItem( HttpServletRequest request, ModelMap model, LoginUser user, @RequestParam String uid ) {
		
		final ReturnBasic topicRes = topicServce.loadTopic( user, uid );
		if( topicRes.isError() ) {
			model.addAttribute("res", topicRes );
			model.addAttribute(AppDefine.KeyShowFooter, false );
		    model.addAttribute(AppDefine.KeyLoadScript, false );
			return "Common/ErrorAlert";
		}
		
		final TopicResultVo topic = (TopicResultVo)topicRes.getData();
		
		model.addAttribute("topic", topic);
		
		if( uid == null ) {
			model.addAttribute(KeySeal, topicServce.createKey(request, user));
		}else if( user != null ) {
			if( topic.getUser().getUid().equals(user.uid() ) ) {
				model.addAttribute(KeySeal, topicServce.createKey(request, user));
			}else {
				model.addAttribute(KeySeal, KeySeal);
			}
		}else {
			model.addAttribute(KeySeal, KeySeal);
		}
		
		return "Topic/TopicItem";
	}

	@GetMapping("InviteUser.html")
	public String inviteUserPage(ModelMap model, LoginUser user, @RequestParam String uid) {
		
		final ReturnBasic topicRes = topicServce.loadTopic(uid);
		if( topicRes.isError() ) {
			model.addAttribute("res", topicRes );
			model.addAttribute(AppDefine.KeyShowFooter, false );
			model.addAttribute(AppDefine.KeyLoadScript, false );
			return "Common/ErrorAlert";
		}

		model.addAttribute("topic", topicRes.getData());
		model.addAttribute("pendingInvite", topicServce.hasPendingInvite(user, uid));
		model.addAttribute("inviter", topicServce.getPendingInviteSourceUser(user, uid));

		return "Topic/InviteUser";
	}
	
	/**
	 * 얘깃거리 저장
	 * @param reqVo
	 * @return
	 */
	@ResponseBody
	@PostMapping("Save.json")
	public ReturnBasic saveTopic( LoginUser user, @RequestBody TopicSaveDto reqVo ) {
		
		return topicServce.saveTopic( user, reqVo );
	}
	
	@ResponseBody
	@GetMapping("List.json")
	public ReturnBasic listTopic( LoginUser user, 
			@RequestParam() ETopicSortType sortType, 
			@RequestParam(required = false, defaultValue = "1") int page, 
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String uid) {
		
		return topicServce.listTopic( user, sortType, page, keyword, uid );
	}
	
	/**
	 * 얘깃거리 언 팔로우 / 팔로우
	 * @param user
	 * @param reqVo
	 * @return
	 */
	@ResponseBody
	@PostMapping("Flow.json")
	public ReturnBasic flow( LoginUser user, @RequestBody TopicDto reqVo ) {
		
		return topicServce.flow( user, reqVo );
	}
	
	/**
	 * 사용자 초대 팝업 화면
	 * @param user
	 * @return
	 */
	@GetMapping("InviteUser.popup")
	public String InviteUserPopup( ModelMap model, LoginUser user, @RequestParam String topicUid ) {
		
		model.addAttribute("topicUid", topicUid);
		
		return "Topic/InviteUserPopup";
	}
	
	/**
	 * 사용자 초대하기
	 * @param user
	 * @param invite
	 * @return
	 */
	@PostMapping("InviteUser.json")
	@ResponseBody
	public ReturnBasic InviteUser( LoginUser user, @RequestBody InviteUserDto invite ) {
		
		return topicServce.inviteUser(user, invite);
	}

	@PostMapping("InviteDecision.json")
	@ResponseBody
	public ReturnBasic inviteDecision(LoginUser user, @RequestBody InviteDecisionDto dto) {
		return topicServce.decideInvite(user, dto);
	}

}
