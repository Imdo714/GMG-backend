package com.gmg.api.member.service.myPage;

import com.gmg.api.member.domain.response.MyPageResponse;

public interface MemberMyPage {
    MyPageResponse getMyPageMemberInfo(Long memberId);

}
