package com.gmg.api.member.service.async;

import com.gmg.api.Participant.domain.response.ParticipantLogListResponse;
import com.gmg.api.Participant.service.ParticipantService;
import com.gmg.api.review.domain.response.RevieweeListResponse;
import com.gmg.api.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
public class MemberAsyncServiceImpl implements MemberAsyncService {

    private final ReviewService reviewService;
    private final ParticipantService participantService;

    @Override
    @Async("defaultTaskExecutor")
    public CompletableFuture<RevieweeListResponse> getRevieweeListByMemberId(Long memberId) {
        return CompletableFuture.completedFuture(
                reviewService.getRevieweeListByMemberId(memberId)
        );
    }

    @Override
    @Async("defaultTaskExecutor")
    public CompletableFuture<ParticipantLogListResponse> getParticipantLogList(Long memberId) {
        return CompletableFuture.completedFuture(
                participantService.getParticipantLogList(memberId)
        );
    }
}
