package com.gmg.api.meeting.service.redis;

import com.gmg.api.meeting.domain.response.MeetingDetailStaticResponse;
import com.gmg.api.meeting.repository.MeetingRepository;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MeetingRedisService {
    private static final String MEETING_VISIT_KEY = "meeting:visit:";
    private static final String MEETING_CACHE_PREFIX = "meeting:detail:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final MeetingRepository meetingRepository;

    // Cache에 값이 있으면 반환
    public MeetingDetailStaticResponse.MeetingDetail getFromCache(Long meetingId) {
        return (MeetingDetailStaticResponse.MeetingDetail)
                redisTemplate.opsForValue().get(MEETING_CACHE_PREFIX + meetingId);
    }

    // DB 조회 후 Redis Cache에 저장
    public MeetingDetailStaticResponse.MeetingDetail getFromDbAndCache(Long meetingId) {
        MeetingDetailStaticResponse.MeetingDetail meeting = getMeetingDetailStatic(meetingId);

        // 캐시에 저장 (TTL 1분)
        redisTemplate.opsForValue().set(MEETING_CACHE_PREFIX + meetingId, meeting, Duration.ofMinutes(1));
        return meeting;
    }

    // Meeting 본문 정적 데이터 DB 조회
    public MeetingDetailStaticResponse.MeetingDetail getMeetingDetailStatic(Long meetingId){
        return meetingRepository.meetingDetailStatic(meetingId)
                .orElseThrow(() -> new ResourceAlreadyExistsException("존재하지 않는 모임입니다."));
    }
}
