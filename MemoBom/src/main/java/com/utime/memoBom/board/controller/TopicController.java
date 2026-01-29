package com.utime.memoBom.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.board.dto.TopicDto;
import com.utime.memoBom.board.dto.TopicSaveDto;
import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.board.vo.ETopicSortType;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.board.vo.query.TopicResultVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("Mosaic")
@RequiredArgsConstructor
public class TopicController {
	
	// Mosaic (조각보)로 바꾸자.
	final TopicService topicServce;
	
	/**
	 * 로그인 화면
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(path = {"", "/", "index.html" })
    public String topicMain( UserVo user ) {
		
		if(topicServce.isEmpty() && user != null ) {
			return "redirect:/Mosaic/Ensemble.html";
		}else {
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
	 * 토픽 상세 보기
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
	 * 토픽 상세 보기
	 * @param reqVo
	 * @return
	 */
	@GetMapping("Item.html")
	public String topicItem( HttpServletRequest request, ModelMap model, LoginUser user, @RequestParam("uid") String uid ) {
		
		final TopicResultVo topic = topicServce.loadTopic( uid );
		if( topic == null ) {
			model.addAttribute("res", new ReturnBasic("E", "사라진 주제입니다.") );
			model.addAttribute(AppDefine.KeyShowFooter, false );
		    model.addAttribute(AppDefine.KeyLoadScript, false );
			return "Common/ErrorAlert";
		}
		
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
	
	/**
	 * 토픽 저장
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
			@RequestParam(required = false) String keyword ) {
		
		return topicServce.listTopic( user, sortType, page, keyword  );
	}
	
	/**
	 * 토픽 정보 읽기
	 * @param uid
	 * @return
	 */
	@ResponseBody
	@GetMapping("LoadMosaic.json")
	public ReturnBasic loadTopic( @RequestParam() String uid ) {
		
		final ReturnBasic result = new ReturnBasic();
		
		TopicResultVo vo = topicServce.loadTopic( uid );
		if( vo == null ) {
			result.setCodeMessage("E", "Mosaic is not found.");
		}else {
			result.setData(vo);
		}

		return result;
	}
	
	/**
	 * 토픽 언 팔로우 / 팔로우
	 * @param user
	 * @param reqVo
	 * @return
	 */
	@ResponseBody
	@PostMapping("Flow.json")
	public ReturnBasic flow( LoginUser user, @RequestBody TopicDto reqVo ) {
		
		return topicServce.flow( user, reqVo );
	}
	
	@GetMapping(path = "Mosaic.html", params = "keyword")
    public String topicSearch( ModelMap model, LoginUser user, @RequestParam() String keyword) {
		model.addAttribute("keyword", keyword );
		return "Topic/TopicMain";
    }

}

