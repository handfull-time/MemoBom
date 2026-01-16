package com.utime.memoBom.common.dao;

/**
 * Key-Value Dao
 */
public interface KeyValueDao {
	
	/**
	 * 키가 있나?
	 * @param k
	 * @return true : 있다.
	 */
	boolean containKey(String k);

	/**
	 * 값 조회
	 * @param k
	 * @return
	 */
	String getValue(String k);
	
	/**
	 * 객체 값 조회
	 * @param k
	 * @param cls
	 * @return
	 */
	<T> T getObject(String k, Class<T> cls) throws RuntimeException;
	
	/**
	 * 값 설정
	 * @param k
	 * @param v
	 * @return
	 */
	int setValue(String k, String v);
	
	/**
	 * 값 설정
	 * @param k
	 * @param v
	 * @return
	 */
	int setValue(String k, String v, int expireMinute);
	
	/**
	 * 객체 값 설정
	 * @param k
	 * @param v
	 * @return
	 */
	int setObject(String k, Object v);
	
	/**
	 * 객체 값 설정
	 * @param k
	 * @param v
	 * @return
	 */
	int setObject(String k, Object v, int expireMinutes);
	
	/**
	 * 키 삭제
	 * @param k
	 * @return
	 */
	int deleteKey(String k);
	
	/**
	 * 키 만료 시간 설정
	 * @param k
	 * @param expireMinute
	 * @return
	 */
	int setExpire(String k, int expireMinutes);
	
	/**
	 * 키 만료 시간 조회
	 * @param k
	 * @return minutes
	 */
	int getExpire(String k);
}
