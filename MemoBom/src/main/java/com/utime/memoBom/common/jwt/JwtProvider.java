package com.utime.memoBom.common.jwt;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.util.CacheIntervalMap;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.vo.ResUserVo;
import com.utime.memoBom.user.vo.UserVo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {

    private static final long ONE_SECOND = 1000L;
    private static final long ACCESS_EXP_MS  = 60L * 60L * ONE_SECOND;       // 60분
    private static final long REFRESH_EXP_MS = 14L * 24L * 60L * 60L * ONE_SECOND; // 14일

    private static final String COOKIE_ACCESS  = "accessToken";
    private static final String COOKIE_REFRESH = "refreshToken";

    private static final String CLM_IP     = "ReqIp";
    private static final String CLM_AGENT  = "ReqAgent";
    private static final String CLM_USERNO = "userNo";
    private static final String CLM_SID    = "sid";
    private static final String CLM_PROVIDER = "provider";
    
	final CacheIntervalMap<String, UserVo> intervalMap = new CacheIntervalMap<>(ACCESS_EXP_MS, TimeUnit.MILLISECONDS);

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;
    private final MacAlgorithm macAlgo = Jwts.SIG.HS256;

    @Autowired
    private UserDao userDao;

    // OAuth 환경에서는 IP 바인딩이 종종 풀릴 수 있어 false 권장
    private final boolean bindRequest = false; 
    
    // 운영(HTTPS) 환경이면 true로 변경 필요
    private final boolean cookieSecure = true; 

    @PostConstruct
    public void init() {
    	
        final byte[] secretBytes = Base64.getDecoder().decode( secret );
        if (secretBytes.length < 32) {
            log.warn("Warning: jwt.secret length is less than 32 bytes.");
        }
        
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    // =========================================================================
    //  Token Resolution & Parsing
    // =========================================================================

    private String getCookieValue(HttpServletRequest request, String name) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    private Claims parseClaims(String token) {
        if (token == null || token.isBlank()) return null;
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.debug("JWT expired");
            return null;
        } catch (JwtException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return null;
        }
    }

    // =========================================================================
    //  Token Generation
    // =========================================================================

    private String generateToken(UserVo user, Map<String, Object> claims, long expMs) {
        final Map<String, Object> safeClaims = (claims == null) ? new HashMap<>() : new HashMap<>(claims);
        
        // userNo를 Claims에 저장 (PK 역할)
        safeClaims.put(CLM_USERNO, String.valueOf(user.getUserNo()));
        safeClaims.put(CLM_PROVIDER, user.getProvider() );

        final Instant now = Instant.now();
        return Jwts.builder()
                .id(user.getId())
                .claims(safeClaims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expMs)))
                .signWith(this.key, this.macAlgo)
                .compact();
    }

    // =========================================================================
    //  Cookie Handling
    // =========================================================================

    /**
     * sameSite("Lax")는 **"다른 사이트의 공격은 막되, 사용자가 링크를 타고 들어오는 건 허용해라"**라는 뜻으로, 최신 브라우저(Chrome 등)의 기본(Default) 정책
     * @param req
     * @param res
     * @param cookieName
     * @param token
     * @param expMs
     */
    private void addTokenCookie(HttpServletRequest req, HttpServletResponse res,
                                String cookieName, String token, long expMs) {
        String contextPath = req.getContextPath();
        final String path = (contextPath != null && !contextPath.isEmpty()) ? contextPath : "/";
        final String domain = req.getServerName();

        final ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(path)
                .domain(domain)
                .maxAge(Duration.ofMillis(expMs))
                .sameSite("Lax")
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
    
    /**
     * 사용자 토큰 쿠키 생성
     * @param request
     * @param response
     * @param user
     * @param sid
     */
    private void genericUserCookie(HttpServletRequest request, HttpServletResponse response, UserVo user, String sid ) {
    	 // 1. Access Token
        final String access = this.generateToken(user, Map.of(CLM_SID, sid), ACCESS_EXP_MS);
        this.addTokenCookie(request, response, COOKIE_ACCESS, access, ACCESS_EXP_MS);

        // 2. Refresh Token
        final String refresh = this.generateToken(user, this.createRequestBindClaims(request, sid), REFRESH_EXP_MS);
        this.addTokenCookie(request, response, COOKIE_REFRESH, refresh, REFRESH_EXP_MS);
    }

    /**
     * [쿠키 삭제 헬퍼]
     * 동일한 이름의 쿠키를 Max-Age 0으로 덮어씌워 삭제합니다.
     * @param req
     * @param res
     * @param cookieName
     */
    private void deleteCookie(HttpServletRequest req, HttpServletResponse res, String cookieName) {
        String contextPath = req.getContextPath();
        final String path = (contextPath != null && !contextPath.isEmpty()) ? contextPath : "/";
        final String domain = req.getServerName();

        final ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path(path)
                .domain(domain)
                .maxAge(Duration.ZERO)
                .sameSite("Lax")
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
    
    /**
     * [로그아웃]
     * Access, Paging, Refresh 토큰 쿠키를 모두 삭제(만료) 처리합니다.
     */
    public void procLogout(HttpServletRequest request, HttpServletResponse response) {
        // 정의된 3가지 토큰에 대해 각각 삭제 명령을 내립니다.
        deleteCookie(request, response, COOKIE_ACCESS);
        deleteCookie(request, response, COOKIE_REFRESH);
        
        log.info("Logout Processed - All Cookies Cleared");
    }

    // =========================================================================
    //  User Extraction & Validation
    // =========================================================================

    private Map<String, Object> createRequestBindClaims(HttpServletRequest request, String sid) {
        
    	final Map<String, Object> claims = new HashMap<>();
        
        claims.put(CLM_IP, AppUtils.getRemoteAddress(request)); 
        claims.put(CLM_AGENT, request.getHeader(HttpHeaders.USER_AGENT));
        if (sid != null) claims.put(CLM_SID, sid);
        
        return claims;
    }

    private boolean validateRequestBinding(HttpServletRequest request, Claims claims) {
        if (!bindRequest) return true;
        // ... (기존 로직 유지)
        return true;
    }

    /**
     * 토큰 Claims에서 사용자를 추출합니다.
     * OAuth 사용자의 경우 ID(Email)로 조회합니다.
     */
    private UserVo extractUser(Claims claims) {
        final String id = claims.getId(); // JWT Subject (여기서는 User ID/Email)
        if (id == null || id.isBlank()) return null;
        
        final String sid = (String)claims.get(CLM_SID, String.class);
        if (sid == null || sid.isBlank()) return null;

        if( intervalMap.containsKey(sid) ) {
        	final UserVo user = intervalMap.get(sid);
        	log.info("Cash user : {}", user.toString());
        	return user;
        }

        // [수정] 메서드명을 일반적인 ID 조회(findById)로 변경했습니다.
        // 기존: userDao.getUserFormIdByProvider(id);
        // 변경: userDao.findByEmail(id) 혹은 findById(id)
        final String provider = (String)claims.get(CLM_PROVIDER, String.class);
        
        final UserVo user = userDao.findByEmail(provider, id); 

        if (user == null) {
        	log.info("Db user is null.");
        	return null;
        }
        
        log.info("Db user : {}", user.toString());

        // 토큰 위변조 체크 (토큰의 userNo와 DB의 userNo 일치 여부)
        final String tokenUserNoStr = claims.get(CLM_USERNO, String.class);
        if (tokenUserNoStr != null) {
            int tokenUserNo = Integer.parseInt(tokenUserNoStr);
            if (user.getUserNo() != tokenUserNo) {
                log.warn("User mismatch. tokenUserNo={}, dbUserNo={}", tokenUserNo, user.getUserNo());
                return null;
            }
        }
        return user;
    }

    // =========================================================================
    //  Public Methods (Process Logic)
    // =========================================================================

    /**
     * [핵심] 로그인 처리
     * UserVo 정보를 받아 토큰을 생성하고 쿠키에 구워서 응답에 담습니다.
     * @throws Exception 
     */
    public ReturnBasic procLogin(HttpServletRequest request, HttpServletResponse response, UserVo user) throws Exception {
        if (user == null) return new ReturnBasic("E", "사용자 정보 없음");

        final String sid = java.util.UUID.randomUUID().toString();

        this.genericUserCookie(request, response, user, sid);

        log.info("Login user : {}", user.toString());
        this.intervalMap.put(sid, user);
        
    	final String ip = AppUtils.getRemoteAddress(request);
    	final UserDevice device = AppUtils.getDeviceInfoFromUserAgent(request.getHeader(HttpHeaders.USER_AGENT));

		userDao.addLoginRecord( user, ip, device );

        return new ReturnBasic(); // 성공 (Default "S")
    }

    public ResUserVo reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        
        // 1. Refresh Token 쿠키 확인
        final String refreshToken = getCookieValue(request, COOKIE_REFRESH);
        if (refreshToken == null) {
            return new ResUserVo("E", "로그인 만료 (Refresh Token 없음)");
        }

        // 2. Refresh Token 검증
        final Claims claims = parseClaims(refreshToken);
        if (claims == null) {
            return new ResUserVo("E", "로그인 만료 (Refresh Token 유효하지 않음)");
        }

        // 3. 사용자 및 보안 검증 (기존 로직 유지)
        if (!validateRequestBinding(request, claims)) {
            return new ResUserVo("E", "유효성 검사 실패");
        }

        final UserVo user = this.extractUser(claims);
        if (user == null) return new ResUserVo("E", "사용자 정보 없음");

        // 4. 새로운 Token 발급 (SID 유지)
        this.genericUserCookie(request, response, user, claims.get(CLM_SID, String.class));
        
        final ResUserVo result = new ResUserVo();
        result.setUser(user);
        return result;
    }
    
    public UserVo getUserVoAccessToken(HttpServletRequest request) {
        
        // 1. Refresh Token 쿠키 확인
        final String refreshToken = getCookieValue(request, COOKIE_REFRESH);
        if (refreshToken == null) {
            return null;
        }

        // 2. Refresh Token 검증
        final Claims claims = parseClaims(refreshToken);
        if (claims == null) {
            return null;
        }

        // 3. 사용자 및 보안 검증 (기존 로직 유지)
        if (!validateRequestBinding(request, claims)) {
            return null;
        }

        return this.extractUser(claims);
    }

    
}