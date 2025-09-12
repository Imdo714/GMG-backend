package com.gmg.api.member.service;

import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.domain.request.LoginDto;
import com.gmg.api.member.domain.request.SingUpDto;
import com.gmg.api.member.domain.response.LoginResponse;
import com.gmg.global.oauth.customHandler.info.OAuth2UserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

public interface MemberService {
    void singUpForm(SingUpDto singUpDto);

    Member socialLogin(OAuth2UserInfo oAuth2UserInfo);

    Optional<Member> securityFindByEmailMember(String email);

    LoginResponse loginForm(LoginDto loginDto);

    LoginResponse GenerateAccessToken(OAuth2User principal);

    Member getMemberById(Long memberId);

}
