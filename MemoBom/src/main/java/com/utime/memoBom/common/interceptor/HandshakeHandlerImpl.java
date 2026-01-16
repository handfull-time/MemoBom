package com.utime.memoBom.common.interceptor;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.utime.memoBom.common.vo.StompPrincipal;

/**
 * 웹 소켓 통신시 고유값 생성 서비스
 */
@Component("StompHandshakeHandler")
class HandshakeHandlerImpl extends DefaultHandshakeHandler {
	
	@Override
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {
		
		Principal result = request.getPrincipal();
		if( result == null ) {
			result = new StompPrincipal( "Bom-" + UUID.randomUUID().toString() );
		}
		
		return result;
	}

}
// 회원 인증이 필수 상황이 온다면 이렇게 처리 할 수 있음.
// 지금은 걍 UUID 를 넘기지만 회원 정보가 오면 회원 id로 호출 가능.
//public class JwtHandshakeHandler extends DefaultHandshakeHandler {
//
//    @Override
//    protected Principal determineUser(ServerHttpRequest request,
//                                      WebSocketHandler wsHandler,
//                                      Map<String, Object> attributes) {
//
//        // 1. 쿠키에서 JWT 추출
//        List<HttpCookie> cookies = ((ServletServerHttpRequest) request)
//                                    .getServletRequest()
//                                    .getCookies() != null ?
//                                    Arrays.asList(((ServletServerHttpRequest) request).getServletRequest().getCookies()) : List.of();
//
//        String jwtToken = cookies.stream()
//                .filter(c -> c.getName().equals("AUTH_TOKEN"))
//                .map(Cookie::getValue)
//                .findFirst()
//                .orElse(null);
//
//        if (jwtToken == null) {
//            System.out.println("❌ JWT 없음, 익명 사용자 처리");
//            return null; // 또는 AnonymousPrincipal 등
//        }
//
//        // 2. JWT 검증 및 사용자 추출
//        String username = JwtUtil.extractUsername(jwtToken); // 검증 + 파싱
//
//        if (username == null) {
//            System.out.println("❌ JWT 유효하지 않음");
//            return null;
//        }
//
//        // 3. Principal 생성
//        return new UsernamePasswordAuthenticationToken(username, null, List.of());
//    }
//}
