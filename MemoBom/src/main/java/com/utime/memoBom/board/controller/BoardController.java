package com.utime.memoBom.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.board.service.BoardService;
import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.board.vo.BoardReqVo;
import com.utime.memoBom.board.vo.TopicVo;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.common.vo.UserDevice;
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
			model.addAttribute("board", boardServce.getBoardList(user) );
			return "Board/BoardMain";
		}
    }

	@GetMapping("New.html")
	public String saveFragment( HttpServletRequest request, ModelMap model, UserVo user, @RequestParam(value="topic") String topicUid ) {
		
		final TopicVo topic = topicServce.loadTopic(topicUid);
		if( topic == null ) {
			model.addAttribute("res", new ReturnBasic("E", "존재하지 않는 주제입니다.") );
			model.addAttribute(AppDefine.KeyShowFooter, false );
			return "Common/ErrorAlert";
		}
		
		final String key = boardServce.createKey(request);
		if( key == null ) {
			model.addAttribute("res", new ReturnBasic("E", "접속하신 장치를 확인해 주세요.") );
			model.addAttribute(AppDefine.KeyShowFooter, false );
			return "Common/ErrorAlert";
		}
		
		model.addAttribute("seal", key );
		model.addAttribute("topic", topicServce.loadTopic(topicUid) );
		
		return "Board/BoardWrite";
	}

	@ResponseBody
	@GetMapping("Save.json")
	public ReturnBasic saveFragment( HttpServletRequest request, UserVo user, UserDevice device, @RequestBody BoardReqVo reqVo ) {
		reqVo.setIp(AppUtils.getRemoteAddress(request));
		
		return boardServce.saveFragment( user, device, reqVo );
	}
	
	@GetMapping(path = "Mosaic.html", params = "uid")
    public String boardTopicFromUid( ModelMap model, UserVo user, @RequestParam("uid") String topicUid ) {
		model.addAttribute("board", boardServce.getTopicBoardListFromTopicUid(user, topicUid) );
		return "Board/BoardMain";
    }
	
	@GetMapping(path = "Mosaic.html", params = "user")
    public String boardTopicFromUser( ModelMap model, UserVo user, @RequestParam("user") String userUid) {
		model.addAttribute("board", boardServce.getTopicBoardListFromUserUid(user, userUid) );
		return "Board/BoardMain";
    }
}

