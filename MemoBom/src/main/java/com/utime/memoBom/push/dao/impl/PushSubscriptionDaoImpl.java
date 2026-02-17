package com.utime.memoBom.push.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.push.dao.PushSubscriptionDao;
import com.utime.memoBom.push.mapper.PushSubscriptionMapper;
import com.utime.memoBom.push.vo.PushSendResVo;
import com.utime.memoBom.push.vo.PushSubVo;
import com.utime.memoBom.push.vo.query.PushSubInfoVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class PushSubscriptionDaoImpl implements PushSubscriptionDao{

	private final PushSubscriptionMapper subMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int savePushSub(PushSubVo vo) throws Exception {
		
		final int result;
		final PushSubInfoVo exist = subMapper.selectPushSubByEndpoint( vo.getEndPoint() );
		
	    if (exist == null) {
	        result = subMapper.insertPushSub(vo);
	    } else {
	        vo.setSubNo(exist.getSubNo());
	        result = subMapper.updatePushSub(vo);
	    }
		
		return result;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int removePushSub(String endPoint) throws Exception {
		
		return subMapper.removeSubscription(endPoint);
	}
	
	@Override
	public List<PushSubInfoVo> findPushSubsByUser(LoginUser user) {
		
		return subMapper.selectActivePushSubsByUser(user.userNo());
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updatePushSubRes(List<PushSendResVo> resList) throws Exception {

		int result = 0;
		for( PushSendResVo item : resList ) {
			final Boolean status = item.status();
			
			if( status == null ) {
				result += subMapper.removeSubscription( item.sub().getEndPoint() );
			}else if( status.booleanValue() ) {
				result += subMapper.markSuccess(item.sub().getSubNo());
			}else {
				if( item.sub().getFailCount() > 5 ) {
					result += subMapper.markInactive( item.sub().getSubNo() );	
				}else {
					result += subMapper.markFail(item.sub().getSubNo());
				}
			}
		}

		return result;
	}

}
