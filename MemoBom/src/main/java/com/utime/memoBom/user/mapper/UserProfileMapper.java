package com.utime.memoBom.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.utime.memoBom.common.vo.BinResultVo;

@Mapper
public interface UserProfileMapper {
    
	BinResultVo selectUserProfile(String uid);
    
	boolean existsUserProfileMeta(long userNo);

    int insertUserProfile(@Param("userNo") long userNo,
                          @Param("mimeType") String mimeType,
                          @Param("image") byte[] image);

    int updateUserProfile(@Param("userNo") long userNo,
                          @Param("mimeType") String mimeType,
                          @Param("image") byte[] image);
}
