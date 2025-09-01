package com.gmg.api.member.service;

import com.gmg.api.member.domain.request.SingUpDto;

public interface MemberService {
    void singUpForm(SingUpDto singUpDto);
}
