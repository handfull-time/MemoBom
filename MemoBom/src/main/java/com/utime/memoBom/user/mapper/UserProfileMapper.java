package com.utime.memoBom.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.user.vo.query.UserProfile;

@Mapper
public interface UserProfileMapper {
    
	UserProfile selectUserProfile(String uid);
    
	boolean existsUserProfileMeta(long userNo);

    int insertUserProfile(@Param("userNo") long userNo,
                          @Param("mimeType") String mimeType,
                          @Param("image") byte[] image);

    int updateUserProfile(@Param("userNo") long userNo,
                          @Param("mimeType") String mimeType,
                          @Param("image") byte[] image);
}
