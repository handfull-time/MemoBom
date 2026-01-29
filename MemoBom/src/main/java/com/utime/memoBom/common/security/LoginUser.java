package com.utime.memoBom.common.security;

import com.utime.memoBom.common.vo.EJwtRole;

public record LoginUser(long userNo, String uid, EJwtRole role) {}