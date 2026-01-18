package com.utime.memoBom.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.utime.memoBom.board.service.BoardService;
import com.utime.memoBom.board.service.TopicService;
import com.utime.memoBom.user.vo.UserVo;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("Board")
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
			return "redirect:/Topic/index.html";
		}else {
			model.addAttribute("board", boardServce.getBoardList(user) );
			return "Board/BoardMain";
		}
    }

	@GetMapping(path = "Topic.html", params = "uid")
    public String boardTopicFromUid( ModelMap model, UserVo user, @RequestParam("uid") String topicUid ) {
		model.addAttribute("board", boardServce.getTopicBoardListFromTopicUid(user, topicUid) );
		return "Board/BoardMain";
    }
	
	@GetMapping(path = "Topic.html", params = "user")
    public String boardTopicFromUser( ModelMap model, UserVo user, @RequestParam("user") String userUid) {
		model.addAttribute("board", boardServce.getTopicBoardListFromUserUid(user, userUid) );
		return "Board/BoardMain";
    }
}

