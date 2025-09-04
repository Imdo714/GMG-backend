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

    private final Long userid;
    private final String username;
    private final String email;

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("userid", userid);
        attributes.put("username", username);
        attributes.put("email", email);
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return username;
    }
}
