package com.gmg.api.review.service;

import com.gmg.api.review.domain.request.ReviewCommentDto;
import com.gmg.api.review.domain.response.RevieweeListResponse;

public interface ReviewService {
    void createReview(Long meetingId, Long targetMemberId, Long writerMemberId, ReviewCommentDto reviewCommentDto);

    RevieweeListResponse getRevieweeListByMemberId(Long memberId);
}
