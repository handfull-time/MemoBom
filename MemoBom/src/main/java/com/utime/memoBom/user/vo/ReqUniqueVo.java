package com.utime.memoBom.user.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utime.memoBom.common.util.AppUtils;

import lombok.Data;

/**
 * 고유 요청 정보
 */
@Data
public class ReqUniqueVo {

	protected String token;
	protected String ip;
	protected String rsaId;
	@JsonIgnore
	protected String publicKey;
	
	@Override
	public String toString() {
		return AppUtils.toJson(this);
	}
}
