package com.gmg.api.member.service.myPage;

import com.gmg.api.Participant.domain.response.ParticipantLogListResponse;
import com.gmg.api.member.domain.response.MyPageResponse;
import com.gmg.api.member.domain.response.dto.MyPageProfileInfoDto;
import com.gmg.api.member.service.MemberService;
import com.gmg.api.member.service.async.MemberAsyncService;
import com.gmg.api.member.service.redis.MemberRedisService;
import com.gmg.api.review.domain.response.RevieweeListResponse;
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

    /**
     *   phases:
     *     - duration: 60
     *       arrivalRate: 10 # 60초 당안 1초에 10번씩 요청 보내다가 점점 30까지 요청을 보냄
     *       rampTo: 30
     *
     * http.response_time.2xx:
     *   min: ......................................................................... 9
     *   max: ......................................................................... 246
     *   mean: ........................................................................ 21.3
     *   median: ...................................................................... 19.9
     *   p95: ......................................................................... 27.9
     *   p99: ......................................................................... 47
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
