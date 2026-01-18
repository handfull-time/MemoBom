package com.utime.memoBom.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.board.vo.TopicReqVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.vo.UserVo;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("Topic")
@RequiredArgsConstructor
public class TopicController {
	
	final TopicService topicServce;
	
	/**
	 * 로그인 화면
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(path = {"", "/", "index.html" })
    public String topicMain( ModelMap model, UserVo user ) {
		
		if( user == null ) {
			return "redirect:/Auth/Login.html";
		}else if(topicServce.isEmpty() ){
			return this.topicItem(model, null);
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
	public ReturnBasic checkSameName( @RequestParam("name")  String name ) {
		
		return topicServce.checkSameName( name );
	}
	
	/**
	 * 토픽 상세 보기
	 * @param reqVo
	 * @return
	 */
	@GetMapping("Item.html")
	public String topicItem( ModelMap model, @RequestParam("uid") String uid ) {
		model.addAttribute("topic", topicServce.loadTopic( uid ));
		return "Topic/TopicItem";
	}
	
	/**
	 * 토픽 저장
	 * @param reqVo
	 * @return
	 */
	@ResponseBody
	@PostMapping("SaveTopic.json")
	public ReturnBasic saveTopic( UserVo user, @RequestBody TopicReqVo reqVo ) {
		
		return topicServce.saveTopic( user, reqVo );
	}
	
	@ResponseBody
	@GetMapping("TopicList.json")
	public ReturnBasic listTopic( UserVo user, 
			@RequestParam(name = "page", required = false, defaultValue = "1") int page, 
			@RequestParam(name = "keyword", required = false) String keyword ) {
		
		final ReturnBasic result = new ReturnBasic();
		
		result.setData( topicServce.listTopic( user, page, keyword  ) );

		return result;
	}
	
	/**
	 * 토픽 정보 읽기
	 * @param uid
	 * @return
	 */
	@ResponseBody
	@GetMapping("LoadTopic.json")
	public ReturnBasic loadTopic( @RequestParam("uid") String uid ) {
		
		final ReturnBasic result = new ReturnBasic();
		
		TopicVo vo = topicServce.loadTopic( uid );
		if( vo == null ) {
			result.setCodeMessage("E", "Topic is not found.");
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
	public ReturnBasic flow( UserVo user, @RequestBody TopicVo reqVo ) {
		
		return topicServce.flow( user, reqVo );
	}
	
	@GetMapping(path = "Topic.html", params = "keyword")
    public String topicSearch( ModelMap model, UserVo user, @RequestParam("keyword") String keyword) {
		model.addAttribute("keyword", keyword );
		return "Topic/TopicMain";
    }

}

