package com.utime.memoBom.push.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.memoBom.push.dao.PushSubscriptionDao;
import com.utime.memoBom.push.mapper.PushSubscriptionMapper;
import com.utime.memoBom.push.vo.PushSubscriptionEntity;
import com.utime.memoBom.user.vo.UserVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class PushSubscriptionDaoImpl implements PushSubscriptionDao{

	private final PushSubscriptionMapper subMapper;
	
	@Override
	public PushSubscriptionEntity findByEndpoint(String endpoint) {
		return subMapper.findByEndpoint( endpoint );
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int save(PushSubscriptionEntity entity) throws Exception {
		
		int result = 0;
		if( entity.getSubNo() < 0L ) {
			result = subMapper.insertSubscription( entity );
		}else {
			result = subMapper.updateSubscription( entity );
		}
		
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int removeSubscription(PushSubscriptionEntity entity) throws Exception {
		
		return subMapper.removeSubscription( entity );
	}

	@Override
	public List<PushSubscriptionEntity> findAllByUser(UserVo user) {

		return subMapper.findAllByUser(user);
	}

	

}
