package com.gmg.api.member.service.myPage;

import com.gmg.api.Participant.domain.response.ParticipantLogListResponse;
import com.gmg.api.Participant.service.ParticipantService;
import com.gmg.api.member.domain.response.MyPageResponse;
import com.gmg.api.member.domain.response.dto.MyPageProfileInfoDto;
import com.gmg.api.member.service.MemberService;
import com.gmg.api.member.service.async.MemberAsyncService;
import com.gmg.api.member.service.redis.MemberRedisService;
import com.gmg.api.review.domain.response.RevieweeListResponse;
import com.gmg.api.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberMyPageImpl implements MemberMyPage {

    private final MemberAsyncService memberAsyncService;
    private  final MemberRedisService memberRedisService;

    private final MemberService memberService;
    private final ReviewService reviewService;
    private final ParticipantService participantService;

    /**
     * - duration: 60
     *      arrivalRate: 10
     *      rampTo: 100
     *
     * http.response_time.2xx:
     *   min: ......................................................................... 6
     *   max: ......................................................................... 1044
     *   mean: ........................................................................ 16.3
     *   median: ...................................................................... 10.9
     *   p95: ......................................................................... 32.8
     *   p99: ......................................................................... 92.8
     *
     *   http.requests: ................................................................. 3300
     *   http.responses: ................................................................ 3300
     * */
//    @Override
//    public MyPageResponse getMyPageMemberInfo(Long memberId) {
//        // 1. 회원 정보 (Redis Cache 설정) : 회원 번호, 이메일, 이름, 프로필
//        MyPageProfileInfoDto emailAndName = memberRedisService.getMemberInfoCache(memberId);
//
//        // 2. 리뷰, 활동 로그 비동기 실행
//        RevieweeListResponse reviewsFuture = reviewService.getRevieweeListByMemberId(memberId);
//        ParticipantLogListResponse activityFuture = participantService.getParticipantLogList(memberId);
//
//        return MyPageResponse.builder()
//                .profileInfo(emailAndName)
//                .reviews(reviewsFuture)
//                .activity(activityFuture)
//                .build();
//    }

    /**
     *  - duration: 60
     *      arrivalRate: 10
     *      rampTo: 100
     *
     *   http.response_time.2xx:
     *   min: ......................................................................... 6
     *   max: ......................................................................... 1305
     *   mean: ........................................................................ 12.9
     *   median: ...................................................................... 8.9
     *   p95: ......................................................................... 16
     *   p99: ......................................................................... 29.1
     *
     *   http.requests: ................................................................. 3300
     *   http.responses: ................................................................ 3300
     * */
    @Override
    public MyPageResponse getMyPageMemberInfo(Long memberId) {
        // 1. 회원 정보 (Redis Cache 설정) : 회원 번호, 이메일, 이름, 프로필
        MyPageProfileInfoDto emailAndName = memberRedisService.getMemberInfoCache(memberId);

        // 2. 리뷰, 활동 로그 비동기 실행
        CompletableFuture<RevieweeListResponse> reviewsFuture =
                memberAsyncService.getRevieweeListByMemberId(memberId);

        CompletableFuture<ParticipantLogListResponse> activityFuture =
                memberAsyncService.getParticipantLogList(memberId);

        // 3. 결과 합치기 (모두 완료될 때까지 대기)
        CompletableFuture.allOf(reviewsFuture, activityFuture).join();

        // 결과 모으기
        RevieweeListResponse reviews = reviewsFuture.join();
        ParticipantLogListResponse activity = activityFuture.join();

        return MyPageResponse.builder()
                .profileInfo(emailAndName)
                .reviews(reviews)
                .activity(activity)
                .build();
    }

}
