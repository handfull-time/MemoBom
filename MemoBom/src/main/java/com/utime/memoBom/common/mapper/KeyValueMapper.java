package com.utime.memoBom.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 최초 필수 테이블 관련 Mapper
 */
@Mapper
public interface KeyValueMapper {
	/**
	 * Key-Value 테이블 생성
	 * @return
	 */
	int createKayValueTable();
	
	/**
	 * 키가 있나?
	 * @param k
	 * @return true : 있다.
	 */
	boolean containKey(@Param("key") String k);
	
	/**
	 * 값 조회
	 * @param k
	 * @return
	 */
	String getValue(@Param("key") String k);
	
	/**
	 * 값 설정
	 * @param k
	 * @param v
	 * @return
	 */
	int setValue(@Param("key") String k, @Param("value") String v, @Param("expireMinute") int expireMinute);
	
	/**
	 * 키 삭제
	 * @param k
	 * @return
	 */
	int deleteKey(@Param("key") String k);
	
	/**
	 * 키 만료 시간 설정
	 * @param k
	 * @param expireMinute
	 * @return
	 */
	int setExpire(@Param("key") String k, @Param("expireMinute") int expireMinutes);
	
	/**
	 * 키 만료 시간 조회
	 * @param k
	 * @return minutes
	 */
	int getExpire(@Param("key") String k);
	
	/**
	 * 만료된 키 삭제
	 * @return
	 */
	int removeExpire();

}