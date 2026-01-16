package com.utime.memoBom.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.common.vo.ReturnBasic;

@Controller
class LiveController  {
	
	@ResponseBody
	@GetMapping("IsLive.json")
	public ReturnBasic isLive() {
		return new ReturnBasic();
	}

}
