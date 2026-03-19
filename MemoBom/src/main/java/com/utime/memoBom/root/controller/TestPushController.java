package com.utime.memoBom.root.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.board.vo.query.ShareDataVo;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.EJwtRole;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.push.service.PushSendService;
import com.utime.memoBom.push.vo.PushSendDataVo;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.vo.query.BasicUserVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("Test")
@RequiredArgsConstructor
public class TestPushController {

    private final PushSendService pushSendService;
    private final UserDao userDao;

    @GetMapping("PushNoti.html")
    public String testPushNoti(Model model) {
        model.addAttribute("assetVersion", AppDefine.AssetVersion);
        return "Test/PushNoti";
    }

    @ResponseBody
    @GetMapping("status.json")
    public ResponseEntity<ReturnBasic> getStatus() throws Exception {
        final ReturnBasic res = pushSendService.getPushStatus(getLoginUser(), "asdfasdf");
        return ResponseEntity.ok().body(res);
    }

    @ResponseBody
    @PostMapping("status.json")
    public ResponseEntity<ReturnBasic> setStatus(@RequestParam boolean enabled) throws Exception {
        final ReturnBasic res = pushSendService.setPushStatus(getLoginUser(), enabled);
        return ResponseEntity.ok().body(res);
    }

    @ResponseBody
    @PostMapping("sendPush.json")
    public ReturnBasic sendPush(@RequestBody String strUserNo) throws Exception {
        final long userNo = Long.parseLong(strUserNo.replace("\"", "").trim());
        final BasicUserVo basicUserVo = userDao.getBasicUserFromUserNo(userNo);

        final LoginUser loginUser = new LoginUser(userNo, basicUserVo.getUid(), EJwtRole.User);

        final PushSendDataVo data = new PushSendDataVo();
        data.setTitle("Mosaic");
        data.setMessage("모자익 좋아요.");
        data.setImageUrl("/MemoBom/images/profile-placeholder.svg");
        data.setLinkUrl("/Mosaic/index.html");

        return pushSendService.sendPush(loginUser, data);
    }

    @ResponseBody
    @PostMapping("sendPush2.json")
    public ReturnBasic sendPushWithData(@RequestBody ShareDataVo data) throws Exception {
        final long userNo = data.getUserNo();
        final BasicUserVo basicUserVo = userDao.getBasicUserFromUserNo(userNo);

        final LoginUser loginUser = new LoginUser(userNo, basicUserVo.getUid(), EJwtRole.User);
        final int res = pushSendService.sendMessageNewFragment(loginUser, data.getUid());
        log.info(String.valueOf(res));

        return new ReturnBasic();
    }

    private LoginUser getLoginUser() {
        return new LoginUser(2L, "a383c637-2bc8-468c-b1a0-2c8b11660fa3", EJwtRole.User);
    }
}
