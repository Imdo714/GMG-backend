package com.gmg.api.member.domain.response;

import com.gmg.api.Participant.domain.response.ParticipantLogListResponse;
import com.gmg.api.member.domain.response.dto.MyPageProfileInfoDto;
import com.gmg.api.review.domain.response.RevieweeListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MyPageResponse {

    private MyPageProfileInfoDto profileInfo; // 1. 회원 정보
    private RevieweeListResponse reviews;     // 2. 리뷰 정보
    private ParticipantLogListResponse activity;  // 3. 활동 기록 정보
}
