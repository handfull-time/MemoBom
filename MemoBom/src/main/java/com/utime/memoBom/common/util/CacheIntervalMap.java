package com.utime.memoBom.common.util;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 일정 시간 지나면 항목 제거 Map<P>
 * 항목을 추가하면 내부에 runnable이 생성되고 interval 시간에 삭제 처리가 이뤄 진다.
 * @param <K>
 * @param <V>
 */
public class CacheIntervalMap<K, V> extends ConcurrentHashMap<K, V>{

	private static final long serialVersionUID = -3116825266165827645L;
	
	/** 스케줄러 서비스 */
	private final ScheduledExecutorService scheduleExe = Executors.newSingleThreadScheduledExecutor();
	
	/** 중간 취소 처리 위한 관리 객체 */
	private final Map<K, ScheduledFuture<K>> callManage = new ConcurrentHashMap<K, ScheduledFuture<K>>();

	/** 지속 시간 */
	private final long interval;
	
	/** 지속 시간 단위 */
	private final TimeUnit unit;
	
	/**
	 * 삭제 될 때 이벤트 처리
	 */
	private final RemoveEventListener<K, V> removelistener;
	
	/**
	 * A task that removes an entry from a `CacheIntervalMap` after a specified delay.
	 *
	 * @param <K> the key type
	 * @param <V> the value type
	 */
	private static class RemoverCashIntervalMap<K, V> implements Callable<K>{
		
		private final CacheIntervalMap<K, V> owner;
		
		private final K key;
		
		public RemoverCashIntervalMap(CacheIntervalMap<K, V> owner, K key) {
			this.owner = owner;
			this.key = key;
		}
		
		@Override
		public K call() throws Exception {
			if( ! this.owner.containsKey(this.key) )
				return null;
			
			// 일단 지우고 
			this.owner.remove(this.key);
			final V v = this.owner.remove(this.key);
			if( this.owner.removelistener != null ) {
				this.owner.removelistener.removeEvent(key, v);
			}

			return this.key;
		}
	}
	
	/**
	 * 캐시 생성
	 * @param interval 일정시간
	 * @param unit 시간 단위
	 */
	public CacheIntervalMap(long interval, TimeUnit unit) {
		this(interval, unit, null);
	}
	
	/**
	 * 캐시 생성
	 * @param interval 일정시간
	 * @param unit 시간 단위
	 * @param event 자동 삭제할때 생기는 이벤트
	 */
	public CacheIntervalMap(long interval, TimeUnit unit, RemoveEventListener<K,V> event) {
		super();
		this.interval = interval;
		this.unit = unit;
		this.removelistener = event;
	}
	
	@Override
	public V remove(Object key) {
		if( this.callManage.containsKey(key) )
			this.callManage.remove( key ).cancel(false);
		
		return super.remove(key);
	}
	
	@Override
	public V put(K key, V value) {
		
		this.scheduledAdd( key );
		
		return super.put(key, value);
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		
		for( K key : m.keySet() ) {
			this.scheduledAdd( key );
		}
		
		super.putAll(m);
	}
	
	/**
	 * 남은 시간 
	 * @param key
	 * @param unit
	 * @return
	 */
	public long getDelay( K key, TimeUnit unit) {
		final ScheduledFuture<K> future = this.callManage.get(key);
		
		return future==null ? 0L:future.getDelay(unit);
	}
	
	/**
     * 지정된 키 일정 추가
     *
     * @param key 제거할 항목의 키
     */
    private void scheduledAdd(K key) {
        final ScheduledFuture<K> future = scheduleExe.schedule(new RemoverCashIntervalMap<>(this, key), interval, unit);
        this.callManage.put(key, future); 
    }
	
	@Override
	public void clear() {
		
		for( Future<K> future : this.callManage.values() ) {
			future.cancel(true);
		}
		
		this.callManage.clear();
		
		super.clear();
	}
	
	/**
     * 스케줄러 서비스 종료
     */
    public void shutdown() {
    	this.scheduleExe.shutdown();
        try {
            if (!this.scheduleExe.awaitTermination(60, TimeUnit.SECONDS)) {
            	this.scheduleExe.shutdownNow();
            }
        } catch (InterruptedException e) {
        	this.scheduleExe.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
	
}