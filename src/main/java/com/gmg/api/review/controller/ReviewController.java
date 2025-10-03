package com.gmg.api.review.controller;

import com.gmg.api.ApiResponse;
import com.gmg.api.review.domain.request.ReviewCommentDto;
import com.gmg.api.review.service.ReviewService;
import com.gmg.global.oauth.jwt.dto.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meeting/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{meetingId}/{targetMemberId}")
    public ApiResponse<String> createReview(
            @PathVariable Long meetingId,
            @PathVariable Long targetMemberId, // 작성 받는 사람
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestBody ReviewCommentDto reviewCommentDto
            ){
        reviewService.createReview(meetingId, targetMemberId, userPrincipal.getMemberId(), reviewCommentDto);
        return ApiResponse.ok("Ok");
    }
}
