package com.utime.memoBom.common.dao.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utime.memoBom.common.dao.KeyValueDao;
import com.utime.memoBom.common.mapper.CommonMapper;
import com.utime.memoBom.common.mapper.KeyValueMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class KeyValueDaoImpl implements KeyValueDao {

	final KeyValueMapper mapper;
	
	final CommonMapper common;
	
	final ObjectMapper objectMapper;
	
	@PostConstruct
	private void postCunstruct() {
		if( ! common.existTable("APP_KV") ) {
			mapper.createKayValueTable();
		}
	}

	@Scheduled(fixedDelay = 60 * 60 * 1000) // 1시간
	public void cleanupExpired() {
	    final int cnt = mapper.removeExpire();
	    if (cnt > 0) {
	        log.info("Expired keys removed: {}", cnt);
	    }
	}
	
	@Override
	public boolean containKey(String k) {
		
		return mapper.containKey(k);
	}
	
	@Override
	public String getValue(String k) {

		return mapper.getValue(k);
	}
	
	@Override
	public String getValueAndRemove(String k) {
		
		final String result = this.getValue(k);
		
		this.deleteKey(k);
		
		return result;
	}

	@Override
	public <T> T getObject(String k, Class<T> cls) throws RuntimeException {
		
		try {
			return objectMapper.readValue(this.getValue(k), cls);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public <T> T getObjectAndRemove(String k, Class<T> cls) throws RuntimeException {
		try {
			return objectMapper.readValue(this.getValueAndRemove(k), cls);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int setValue(String k, String v) {

		return this.setValue(k, v, 0);
	}

	@Override
	public int setValue(String k, String v, int expireMinute) {

		return mapper.setValue(k, v, expireMinute);
	}

	@Override
	public int setObject(String k, Object v) {
		
		return this.setObject(k, v, 0);
	}

	@Override
	public int setObject(String k, Object v, int expireMinutes) {
		
		int result = 0;
		try {
			result = this.setValue(k, objectMapper.writeValueAsString(v), expireMinutes);
		} catch (JsonProcessingException e) {
			log.error("setObject error:", e);
			result = -1;
		}
		
		return result;
	}

	@Override
	public int deleteKey(String k) {
		
		return mapper.deleteKey(k);
	}

	@Override
	public int setExpire(String k, int expireMinutes) {
		
		return mapper.setExpire(k, expireMinutes);
	}

	@Override
	public int getExpire(String k) {
		
		return mapper.getExpire(k);
	}

}
