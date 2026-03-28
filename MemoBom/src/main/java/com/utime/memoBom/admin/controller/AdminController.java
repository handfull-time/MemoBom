package com.utime.memoBom.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.admin.service.AdminService;
import com.utime.memoBom.admin.vo.AdminSearchVo;
import com.utime.memoBom.admin.vo.AdminTopicVo;
import com.utime.memoBom.admin.vo.AdminUserVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.ReturnBasic;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("Lotus")
@RequiredArgsConstructor
public class AdminController {
	
	final AdminService adminService;
	
	/**
	 * 관리자 로그인 전환을 처리한다.
	 *
	 * @param request HTTP 요청
	 * @param response HTTP 응답
	 * @param model 모델
	 * @param user 현재 사용자
	 * @return 리다이렉트 경로
	 * @throws Exception 로그인 처리 중 예외
	 */
	@GetMapping(path = {"Login.html" })
    public String adminLogin( HttpServletRequest request, HttpServletResponse response, ModelMap model, LoginUser user ) throws Exception {

		if( ! this.adminService.adminLogin(request, response, user).isError() ) {
			return "redirect:/Lotus/index.html";
		}else {
			return "redirect:/";
		}
    }
	
	/**
	 * 관리자 로그아웃(원 사용자 복귀)을 처리한다.
	 *
	 * @param request HTTP 요청
	 * @param response HTTP 응답
	 * @param model 모델
	 * @param user 현재 사용자
	 * @return 리다이렉트 경로
	 * @throws Exception 로그아웃 처리 중 예외
	 */
	@GetMapping(path = {"Logout.html" })
    public String adminLogout( HttpServletRequest request, HttpServletResponse response, ModelMap model, LoginUser user ) throws Exception {

		this.adminService.adminLogout(request, response);
		
		return "redirect:/";
    }

	/**
	 * 관리자 대시보드 화면을 반환한다.
	 *
	 * @param model 모델
	 * @param user 현재 사용자
	 * @param date 조회 일자(yyyy-MM-dd)
	 * @return 대시보드 템플릿 경로
	 */
	@GetMapping(path = {"", "/", "index.html" })
    public String adminMain( ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("item", adminService.getDashboard(date) );
		model.addAttribute("date", date );
		
		return "Admin/AdminDashBoard";
    }

	/**
	 * 관리자 대시보드 집계 정보를 JSON으로 반환한다.
	 *
	 * @param user 현재 사용자
	 * @param date 조회 일자(yyyy-MM-dd)
	 * @return 대시보드 응답 데이터
	 */
	@ResponseBody
	@GetMapping(path = {"Dashboard.json"})
	public ReturnBasic getDashboard(LoginUser user, @RequestParam(required = false) String date) {
		final ReturnBasic result = new ReturnBasic();
		result.setData(adminService.getDashboard(date));
		return result;
	}

	/**
	 * 사용자 관리 화면을 반환한다.
	 *
	 * @param model 모델
	 * @param user 현재 사용자
	 * @param date 조회 일자(yyyy-MM-dd)
	 * @return 사용자 관리 템플릿 경로
	 */
	@GetMapping(path = {"User.html" })
    public String adminUsers(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {

		model.addAttribute("date", date );
		return "Admin/AdminUser";
    }
	
	/**
	 * 사용자 목록을 JSON으로 반환한다.
	 *
	 * @param user 현재 사용자
	 * @param searchVo 조회 조건
	 * @return 사용자 목록 응답
	 */
	@ResponseBody
	@GetMapping(path = {"UserList.json" })
	public ReturnBasic getUserList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getUserList(user, searchVo );
	}

	/**
	 * 사용자 메모(note)를 저장한다.
	 *
	 * @param user 현재 사용자
	 * @param userVo 사용자 정보(userNo, note)
	 * @return 저장 결과
	 */
	@ResponseBody
	@PostMapping(path = {"UserNoteSave.json" })
	public ReturnBasic saveUserNote(LoginUser user, @RequestBody AdminUserVo userVo ) {
		return adminService.updateUserNote(user, userVo);
	}

	/**
	 * 사용자 활성화(enabled)를 변경한다.
	 *
	 * @param user 현재 사용자
	 * @param userVo 사용자 정보(userNo, enabled)
	 * @return 변경 결과
	 */
	@ResponseBody
	@PostMapping(path = {"UserEnabledSave.json" })
	public ReturnBasic saveUserEnabled(LoginUser user, @RequestBody AdminUserVo userVo ) {
		return adminService.updateUserEnabled(user, userVo);
	}

	/**
	 * 토픽 관리 화면을 반환한다.
	 *
	 * @param model 모델
	 * @param user 현재 사용자
	 * @param date 조회 일자(yyyy-MM-dd)
	 * @return 토픽 관리 템플릿 경로
	 */
	@GetMapping(path = {"Topic.html" })
    public String adminTopics(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("date", date );
		return "Admin/AdminTopic";
    }

	/**
	 * 토픽 팔로우 사용자 목록 팝업을 반환한다.
	 *
	 * @param model 모델
	 * @param user 현재 사용자
	 * @param topicNo 토픽 번호
	 * @return 팝업 템플릿 경로
	 */
	@GetMapping(path = {"TopicFollow.popup" })
	public String adminTopicFollowPopup(ModelMap model, LoginUser user, @RequestParam Long topicNo) {
		final ReturnBasic result = adminService.getTopicFollowUsers(user, topicNo);
		model.addAttribute("topicNo", topicNo);
		model.addAttribute("result", result);
		model.addAttribute("followers", result.getData());
		return "Admin/TopicFollowPopup";
	}
	
	/**
	 * 토픽 목록을 JSON으로 반환한다.
	 *
	 * @param user 현재 사용자
	 * @param searchVo 조회 조건
	 * @return 토픽 목록 응답
	 */
	@ResponseBody
	@GetMapping(path = {"TopicList.json" })
	public ReturnBasic getTopicList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getTopicList(user, searchVo );
	}

	/**
	 * 토픽 수정 가능 항목(ENABLED, MAX_LEN, AI, PROMPT)을 저장한다.
	 *
	 * @param user 현재 사용자
	 * @param topicVo 토픽 수정 값
	 * @return 저장 결과
	 */
	@ResponseBody
	@PostMapping(path = {"TopicSave.json" })
	public ReturnBasic saveTopicEditable(LoginUser user, @RequestBody AdminTopicVo topicVo) {
		return adminService.updateTopicEditable(user, topicVo);
	}
	
	/**
	 * 글(프래그먼트) 관리 화면을 반환한다.
	 *
	 * @param model 모델
	 * @param user 현재 사용자
	 * @param date 조회 일자(yyyy-MM-dd)
	 * @return 글 관리 템플릿 경로
	 */
	@GetMapping(path = {"Fragment.html" })
    public String adminFragments(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("date", date );
		return "Admin/AdminFragment";
    }
	
	/**
	 * 글(프래그먼트) 목록을 JSON으로 반환한다.
	 *
	 * @param user 현재 사용자
	 * @param searchVo 조회 조건
	 * @return 글 목록 응답
	 */
	@ResponseBody
	@GetMapping(path = {"FragmentList.json" })
	public ReturnBasic getFragmentList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getFragmentList(user, searchVo );
	}
	
	/**
	 * 댓글 관리 화면을 반환한다.
	 *
	 * @param model 모델
	 * @param user 현재 사용자
	 * @param date 조회 일자(yyyy-MM-dd)
	 * @return 댓글 관리 템플릿 경로
	 */
	@GetMapping(path = {"Comment.html" })
    public String adminComments(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("date", date );
		return "Admin/AdminComment";
    }
	
	/**
	 * 댓글 목록을 JSON으로 반환한다.
	 *
	 * @param user 현재 사용자
	 * @param searchVo 조회 조건
	 * @return 댓글 목록 응답
	 */
	@ResponseBody
	@GetMapping(path = {"CommentList.json" })
	public ReturnBasic getCommentList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getCommentList(user, searchVo );
	}

	/**
	 * 프래그먼트 미리보기 팝업을 반환한다.
	 *
	 * @param model 모델
	 * @param user 현재 사용자
	 * @param fragmentNo 프래그먼트 번호
	 * @return 팝업 템플릿 경로
	 */
	@GetMapping(path = {"FragmentPreview.popup" })
	public String adminFragmentPreviewPopup(ModelMap model, LoginUser user, @RequestParam Long fragmentNo) {
		final ReturnBasic result = adminService.getFragmentPreview(user, fragmentNo);
		model.addAttribute("result", result);
		model.addAttribute("item", result.getData());
		return "Admin/FragmentPreviewPopup";
	}
	
	/**
	 * 이모션 관리 화면을 반환한다.
	 *
	 * @param model 모델
	 * @param user 현재 사용자
	 * @param date 조회 일자(yyyy-MM-dd)
	 * @return 이모션 관리 템플릿 경로
	 */
	@GetMapping(path = {"Emotion.html" })
    public String adminEmotions(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("date", date );
		return "Admin/AdminEmotion";
    }
	
	/**
	 * 이모션 목록을 JSON으로 반환한다.
	 *
	 * @param user 현재 사용자
	 * @param searchVo 조회 조건
	 * @return 이모션 목록 응답
	 */
	@ResponseBody
	@GetMapping(path = {"EmotionList.json" })
	public ReturnBasic getEmotionList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getEmotionList(user, searchVo );
	}
	
	/**
	 * 스크랩 관리 화면을 반환한다.
	 *
	 * @param model 모델
	 * @param user 현재 사용자
	 * @param date 조회 일자(yyyy-MM-dd)
	 * @return 스크랩 관리 템플릿 경로
	 */
	@GetMapping(path = {"Scrap.html" })
    public String adminScrap(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("date", date );
		return "Admin/AdminScrap";
    }
	
	/**
	 * 스크랩 목록을 JSON으로 반환한다.
	 *
	 * @param user 현재 사용자
	 * @param searchVo 조회 조건
	 * @return 스크랩 목록 응답
	 */
	@ResponseBody
	@GetMapping(path = {"ScrapList.json" })
	public ReturnBasic getScrapList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getScrapList(user, searchVo );
	}
	
	/**
	 * 공유 관리 화면을 반환한다.
	 *
	 * @param model 모델
	 * @param user 현재 사용자
	 * @param date 조회 일자(yyyy-MM-dd)
	 * @return 공유 관리 템플릿 경로
	 */
	@GetMapping(path = {"Share.html" })
    public String adminShares(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("date", date );
		return "Admin/AdminShare";
    }
	
	/**
	 * 공유 목록을 JSON으로 반환한다.
	 *
	 * @param user 현재 사용자
	 * @param searchVo 조회 조건
	 * @return 공유 목록 응답
	 */
	@ResponseBody
	@GetMapping(path = {"ShareList.json" })
	public ReturnBasic getShareList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getShareList(user, searchVo );
	}
	
	/**
	 * 푸시 관리 화면을 반환한다.
	 *
	 * @param model 모델
	 * @param user 현재 사용자
	 * @param date 조회 일자(yyyy-MM-dd)
	 * @return 푸시 관리 템플릿 경로
	 */
	@GetMapping(path = {"Push.html" })
    public String adminPush(ModelMap model, LoginUser user, @RequestParam(required = false) String date ) {
		
		model.addAttribute("date", date );
		return "Admin/AdminPush";
    }
	
	/**
	 * 푸시 목록을 JSON으로 반환한다.
	 *
	 * @param user 현재 사용자
	 * @param searchVo 조회 조건
	 * @return 푸시 목록 응답
	 */
	@ResponseBody
	@GetMapping(path = {"PushList.json" })
	public ReturnBasic getPushList(LoginUser user, AdminSearchVo searchVo ) {
		
		return adminService.getPushList(user, searchVo );
	}
	
	/**
	 * 설정 화면을 반환한다.
	 *
	 * @param model 모델
	 * @param user 현재 사용자
	 * @return 설정 화면 템플릿 경로
	 */
	@GetMapping(path = {"Setting.html" })
    public String adminSettings(ModelMap model, LoginUser user ) {
		
		return "Admin/AdminSetting";
    }
	
}
