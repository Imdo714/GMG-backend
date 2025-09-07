package com.gmg.api.member.domain.response;

import com.gmg.api.member.domain.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResponse {

    private String accessToken;
    private Long userId;
    private String userName;

    public static LoginResponse of(String accessToken, Member member){
        return LoginResponse.builder()
                .accessToken(accessToken)
                .userId(member.getMemberId())
                .userName(member.getName())
                .build();
    }
}
