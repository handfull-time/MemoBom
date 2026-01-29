package com.utime.memoBom.user.service.impl;

import java.security.KeyPair;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.utime.memoBom.common.dao.KeyValueDao;
import com.utime.memoBom.common.security.JwtProvider;
import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.util.CacheIntervalMap;
import com.utime.memoBom.common.util.RsaEncDec;
import com.utime.memoBom.common.vo.ReturnBasic;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.service.AuthService;
import com.utime.memoBom.user.vo.ReqUniqueVo;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class AuthServiceImpl implements AuthService {
	
	private final CacheIntervalMap<String, String> intervalMap = new CacheIntervalMap<>(10L, TimeUnit.MINUTES);
	
	final UserDao userDao;
	final KeyValueDao kvDao;
	final JwtProvider jwtProvider;
	
	@PostConstruct
	private void init() throws Exception{
		
	}
	
	/**
	 * Interval 에 추가.
	 * @param value
	 * @return 추가 key
	 */
	private String inputInterval( String value ) {
		
		if( value == null ) {
			return null;
		}
		
		final UUID guid = UUID.randomUUID();
		 
		final String result = guid.toString();
		
		this.intervalMap.put(result, value);
		
		log.info("interval 추가: {} - {}", result, ((value.length()>128)? (value.substring(0, 127)+"..."):value) );

		return result;
	}
	
	@Override
	public ReqUniqueVo getNewGenUnique(HttpServletRequest request) {
		
		final ReqUniqueVo result = new ReqUniqueVo();
		result.setToken( this.inputInterval( AppUtils.getRemoteAddress( request ) ) );
		
		final KeyPair pair = RsaEncDec.generateRSAKeyPair();
		result.setPublicKey( RsaEncDec.getPulicKeyScript(pair) );
		result.setRsaId( this.inputInterval( RsaEncDec.getPrivateKey(pair) ) );
		
		return result;
	}
	
	@Override
	public ReturnBasic logout(HttpServletRequest request, HttpServletResponse response, LoginUser user) {
		
		jwtProvider.procLogout(request, response);
		
		return new ReturnBasic();
	}

}
