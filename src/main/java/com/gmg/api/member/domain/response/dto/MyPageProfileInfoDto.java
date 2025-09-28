package com.gmg.api.member.domain.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyPageProfileInfoDto {

    private Long memberId;
    private String email;
    private String name;
    private String profile;
}
