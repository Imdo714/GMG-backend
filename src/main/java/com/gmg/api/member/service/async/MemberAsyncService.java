package com.gmg.api.member.service.async;

import com.gmg.api.member.domain.entity.Member;

public interface MemberAsyncService {

    void saveMember(Member member);
}
