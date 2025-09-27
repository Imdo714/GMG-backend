package com.gmg.api.review.service;

import com.gmg.api.review.domain.request.ReviewCommentDto;

public interface ReviewService {
    void createReview(Long meetingId, Long targetMemberId, Long writerMemberId, ReviewCommentDto reviewCommentDto);

}
