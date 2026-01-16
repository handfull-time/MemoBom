package com.utime.memoBom.common.util;

/**
 * 아이템이 제거될 때 발생할 이벤트
 * @param <K>
 * @param <V>
 */
public interface RemoveEventListener<K, V> {

	void removeEvent(K k, V v);
}
