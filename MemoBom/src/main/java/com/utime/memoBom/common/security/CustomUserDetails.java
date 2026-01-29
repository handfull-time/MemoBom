package com.utime.memoBom.common.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.utime.memoBom.user.vo.UserVo;
import com.utime.memoBom.common.vo.EJwtRole;

import lombok.Getter;
import lombok.ToString;

/**
 * Spring Security principal.
 * DTO/VO가 아니라 SecurityContext에 저장되는 인증 주체 객체입니다.
 */
@Getter
@ToString
public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;
    private static final String ROLE_PREFIX = "ROLE_";

    private final long userNo;
    private final boolean enabled;
    private final EJwtRole role;

    private final String provider;
    private final String uid;
    private final String id;
    private final String email;
    private final String nickname;
    private final String profileUrl;

    private CustomUserDetails(long userNo, boolean enabled, EJwtRole role,
                              String provider, String uid, String id, String email,
                              String nickname, String profileUrl) {
        this.userNo = userNo;
        this.enabled = enabled;
        this.role = role;

        this.provider = provider;
        this.uid = uid;
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }

    public static CustomUserDetails from(UserVo vo) {
        if (vo == null) return null;
        return new CustomUserDetails(
                vo.getUserNo(),
                vo.isEnabled(),
                vo.getRole(),
                vo.getProvider(),
                vo.getUid(),
                vo.getId(),
                vo.getEmail(),
                vo.getNickname(),
                vo.getProfileUrl()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = (role == null) ? "User" : role.name();
        return Collections.singleton(new SimpleGrantedAuthority(ROLE_PREFIX + roleName));
    }

    /**
     * Spring Security에서 username으로 쓰는 값.
     * 로그인 식별자 정책에 맞춰 id/email/userNo 중 하나를 선택하세요.
     */
    @Override
    public String getUsername() {
        if (id != null && !id.isBlank()) return id;
        if (email != null && !email.isBlank()) return email;
        return String.valueOf(userNo);
    }

    @Override
    public String getPassword() {
        // JWT 기반이면 password를 보관할 필요가 없습니다.
        return null;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }
}
