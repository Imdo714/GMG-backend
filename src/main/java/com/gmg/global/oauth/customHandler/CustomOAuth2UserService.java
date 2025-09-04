package com.gmg.global.oauth.customHandler;

import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.service.MemberService;
import com.gmg.global.oauth.customHandler.dto.CustomUser;
import com.gmg.global.oauth.customHandler.info.GoogleUserInfo;
import com.gmg.global.oauth.customHandler.info.OAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberService memberService;

    public CustomOAuth2UserService(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // google, kakao, naver
        OAuth2User oAuth2User = super.loadUser(userRequest); // 정보

        OAuth2UserInfo oAuth2UserInfo;
        if("google".equals(registrationId)){
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("허용되지 않는 인증입니다.");
        }

        Member member = memberService.securityFindByEmailMember((String) oAuth2User.getAttribute("email"))
                .orElseGet(() -> memberService.socialLogin(oAuth2UserInfo));

        return new CustomUser(member.getMemberId(), member.getName(), member.getEmail());
    }

}
