package com.utime.memoBom.root.controller;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.EJwtRole;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.push.service.PushSendService;
import com.utime.memoBom.push.vo.PushSendDataVo;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.vo.query.BasicUserVo;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("TestPush")
@RequiredArgsConstructor
public class TestPushController {

    private final PushSendService pushSendService;
    private final UserDao userDao;

    @GetMapping("PushNoti.html")
    public String pushNoti(Model model) {
        model.addAttribute("assetVersion", AppDefine.AssetVersion);
        return "Test/PushNoti";
    }

    @ResponseBody
    @GetMapping("Status.json")
    public ResponseEntity<ReturnBasic> getStatus(LoginUser user, @RequestParam String deviceId) throws Exception {
        final ReturnBasic res = pushSendService.getPushStatus(user, deviceId);
        return ResponseEntity.ok().body(res);
    }

    @ResponseBody
    @PostMapping("Status.json")
    public ResponseEntity<ReturnBasic> setStatus(LoginUser user, @RequestParam boolean enabled) throws Exception {
        final ReturnBasic res = pushSendService.setPushStatus(user, enabled);
        return ResponseEntity.ok().body(res);
    }

    @ResponseBody
    @PostMapping("SendPush.json")
    public ReturnBasic sendPush(@RequestBody String strUserNo) throws Exception {
        final long userNo = Long.parseLong(strUserNo);
        final BasicUserVo basicUserVo = userDao.getBasicUserFromUserNo(userNo);

        final LoginUser loginUser = new LoginUser(userNo, basicUserVo.getUid(), EJwtRole.User);

        final PushSendDataVo data = new PushSendDataVo();
        data.setTitle("MemoBom Push Test");
        data.setMessage("브라우저 푸시 발송 테스트입니다.");
        data.setImageUrl("/MemoBom/images/profile-placeholder.svg");
        data.setLinkUrl("/");

        return pushSendService.sendPush(loginUser, data);
    }

    @ResponseBody
    @PostMapping("GenerateVapidKey.json")
    public ReturnBasic generateVapidKey() {
        try {
            final ReturnBasic res = new ReturnBasic(AppDefine.ERROR_OK, "ok");
            res.setData(createVapidKeys());
            return res;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            return new ReturnBasic("E", e.getMessage());
        }
    }

    /** EC 공개키 → uncompressed point (0x04 + X + Y) */
    private static byte[] encodePublicKey(ECPublicKey publicKey) {
        final byte[] x = bigIntTo32Bytes(publicKey.getW().getAffineX());
        final byte[] y = bigIntTo32Bytes(publicKey.getW().getAffineY());

        final byte[] encoded = new byte[65];
        encoded[0] = 0x04;
        System.arraycopy(x, 0, encoded, 1, 32);
        System.arraycopy(y, 0, encoded, 33, 32);
        return encoded;
    }

    /** BigInteger → 32 bytes */
    private static byte[] bigIntTo32Bytes(BigInteger value) {
        final byte[] src = value.toByteArray();
        final byte[] dst = new byte[32];

        if (src.length > 32) {
            System.arraycopy(src, src.length - 32, dst, 0, 32);
        } else {
            System.arraycopy(src, 0, dst, 32 - src.length, src.length);
        }
        return dst;
    }

    private static Map<String, String> createVapidKeys() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(nl.martijndwars.webpush.Utils.ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
        keyPairGenerator.initialize(new ECGenParameterSpec(nl.martijndwars.webpush.Utils.CURVE));

        final KeyPair keyPair = keyPairGenerator.generateKeyPair();

        final ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        final ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

        final byte[] publicKeyBytes = encodePublicKey(publicKey);
        final byte[] privateKeyBytes = bigIntTo32Bytes(privateKey.getS());

        final Map<String, String> result = new HashMap<>();
        result.put("publicKey", Base64.getUrlEncoder().withoutPadding().encodeToString(publicKeyBytes));
        result.put("privateKey", Base64.getUrlEncoder().withoutPadding().encodeToString(privateKeyBytes));
        return result;
    }
}

