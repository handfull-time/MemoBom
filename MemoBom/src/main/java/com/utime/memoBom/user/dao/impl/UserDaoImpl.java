package com.utime.memoBom.user.dao.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.utime.memoBom.common.security.LoginUser;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.vo.AppDefine;
import com.utime.memoBom.common.vo.UserDevice;
import com.utime.memoBom.user.dao.UserDao;
import com.utime.memoBom.user.mapper.UserMapper;
import com.utime.memoBom.user.mapper.UserProfileMapper;
import com.utime.memoBom.user.vo.EFontSize;
import com.utime.memoBom.user.vo.MyWriterVo;
import com.utime.memoBom.user.vo.UserVo;
import com.utime.memoBom.user.vo.query.BasicUserVo;
import com.utime.memoBom.user.vo.query.UsageStatisticsVo;
import com.utime.memoBom.user.vo.query.UserProfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
class UserDaoImpl implements UserDao {
	
	private final UserMapper userMapper;
	private final UserProfileMapper profileMapper;
	
	@Value("${appName}")
	private String appName;
	
	@Override
	public UserVo findById(String provider, String id) {

		return userMapper.selectUserFromIdAndProvider( provider, id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int addUser(UserVo user) throws Exception {
		if( user == null ) {
			throw new Exception("user is null ");
		}
		
		final int result = userMapper.insertUser( user );
		
		if( result > 0 ) {
			final UserVo dbUser = userMapper.selectUserFromUserNo(user.getUserNo());
			BeanUtils.copyProperties(dbUser, user);
		}
		
		return result;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int addLoginRecord(UserVo result, String ip, UserDevice device) throws Exception {
		
		return userMapper.insertLoginRecord(result, ip, device);
	}
	
	@Override
	public int deleteUser(UserVo userVo) throws Exception {
		
		return userMapper.removeUser(userVo);
	}
	
	@Override
	public UserVo getUserFromUid(String uid) {
		
		return userMapper.selectUserFromUid(uid);
	}
	
	@Override
	public List<MyWriterVo> getMyWriteDataList(LoginUser user, String date) {
		
		return userMapper.selectMyWriteDataList(user, date);
	}
	
	@Override
	public BasicUserVo getBasicUserFromUserNo(long userNo) {
		
		return userMapper.getBasicUserFromUserNo(userNo);
	}
	
	@Override
	public UsageStatisticsVo getUserStatisticsRecord(long userNo) {
		
		return userMapper.selectUserRecord(userNo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateUserInfo(LoginUser user, String nickname, MultipartFile profile) throws Exception {

	    int updated = 0;

	    // 1) 닉네임
	    if (AppUtils.isNotEmpty(nickname)) {
	        updated += userMapper.updateNicname(user, nickname);
	    }
	    
	    // 2) 프로필 이미지
	    if (profile != null && !profile.isEmpty()) {

	        // (권장) 용량 제한 같은 방어
	        // if (profile.getSize() > 2_000_000) throw new IllegalArgumentException("이미지 용량이 너무 큽니다.");

	        final String mimeType = (profile.getContentType() != null) ? profile.getContentType() : "application/octet-stream";
	        final byte[] imageBytes = profile.getBytes();

	        // 존재 여부 확인 (이미지 BLOB까지 읽을 필요 없으면 meta 조회 권장)
	        final boolean exists = profileMapper.existsUserProfileMeta(user.userNo());

	        if (! exists) {
	            updated += profileMapper.insertUserProfile(user.userNo(), mimeType, imageBytes);
	            updated += userMapper.updateProfile(user, "/" + appName + AppDefine.KeyUserImage + user.uid());
	        } else {
	            updated += profileMapper.updateUserProfile(user.userNo(), mimeType, imageBytes);
	        }
	    }

	    return updated;
	}
	
	@Override
	public UserProfile getUserProfile(String uid) {
		
		return profileMapper.selectUserProfile(uid);
	}
	
	@Override
	public Boolean getPushStatus(LoginUser user, String deviceId) {
		
		return userMapper.selectPushStatus(user, deviceId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int setPushStatus(LoginUser user, boolean enabled) throws Exception {
		
		return userMapper.updatePushStatus(user, enabled);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateMyInfoFontSize(LoginUser user, EFontSize fs) throws Exception {
		
		return userMapper.updateMyInfoFontSize(user, fs);
	}

}
