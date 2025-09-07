package com.gmg.api.member.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginDto {

    private String email;
    private String password;
}
