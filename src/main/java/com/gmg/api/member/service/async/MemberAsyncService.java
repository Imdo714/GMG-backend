package com.gmg.api.member.service.async;

import com.gmg.api.Participant.domain.response.ParticipantLogListResponse;
import com.gmg.api.review.domain.response.RevieweeListResponse;

import java.util.concurrent.CompletableFuture;

public interface MemberAsyncService {

    CompletableFuture<RevieweeListResponse> getRevieweeListByMemberId(Long memberId);

    CompletableFuture<ParticipantLogListResponse> getParticipantLogList(Long memberId);
}
