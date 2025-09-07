package com.gmg.global.oauth.customHandler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class CustomUser implements OAuth2User {

    // SpringSecurity 에서 소셜로그인 정보를 CustomUser 에 저장
    private final Long memberId;
    private final String name;
    private final String email;

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("memberId", memberId);
        attributes.put("name", name);
        attributes.put("email", email);
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return name;
    }
}
