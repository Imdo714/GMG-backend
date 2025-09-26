package com.gmg.api.meeting.service.redis;

import com.gmg.api.meeting.domain.response.MeetingDetailStaticResponse;
import com.gmg.api.meeting.repository.MeetingRepository;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeetingRedisService {
    private static final String MEETING_CACHE_PREFIX = "meeting:detail:";
    private static final String MEETING_VISIT_KEY = "meeting:visit:";
    private static final String DIRTY_MEETINGS_KEY = "dirty:meetings";
    private static final Integer TTL_MINUTE_MEETING_CACHE = 10;
    private static final Integer TTL_MINUTE_SEE_COUNT_CACHE = 10;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final MeetingRepository meetingRepository;

    // 미팅 본문 Cache에 값이 있으면 반환
    public MeetingDetailStaticResponse.MeetingDetail getFromCache(Long meetingId) {
        return (MeetingDetailStaticResponse.MeetingDetail)
                redisTemplate.opsForValue().get(MEETING_CACHE_PREFIX + meetingId);
    }

    // DB 조회 후 Redis Cache에 저장
    public MeetingDetailStaticResponse.MeetingDetail getFromDbAndCache(Long meetingId) {
        MeetingDetailStaticResponse.MeetingDetail meeting = getMeetingDetailStatic(meetingId);

        // 캐시에 저장 (TTL 1분)
        redisTemplate.opsForValue().set(MEETING_CACHE_PREFIX + meetingId, meeting, Duration.ofMinutes(TTL_MINUTE_MEETING_CACHE));
        return meeting;
    }

    // Meeting 본문 정적 데이터 DB 조회
    public MeetingDetailStaticResponse.MeetingDetail getMeetingDetailStatic(Long meetingId){
        return meetingRepository.meetingDetailStatic(meetingId)
                .orElseThrow(() -> new ResourceAlreadyExistsException("존재하지 않는 모임입니다."));
    }

    // 조회수 Cache에 조회수 없으면 Cache에 저장
    public void validateAndCacheSeeCount(Long meetingId){
        String key = MEETING_VISIT_KEY + meetingId;

        Object currentCountObj = stringRedisTemplate.opsForValue().get(key);
        if(currentCountObj == null){
            Long seenCountValue = getSeeCountValue(meetingId);// 요거 조회수만 조회
            stringRedisTemplate.opsForValue().set(key, String.valueOf(seenCountValue), Duration.ofMinutes(TTL_MINUTE_SEE_COUNT_CACHE)); // DB 값 세팅
        }
    }

    // 조회수 Cache에 조회수 값 1증가 후 값 반환
    public Long incrementSeeCount(Long meetingId){
        String key = MEETING_VISIT_KEY + meetingId;

        Long incrementedCount = stringRedisTemplate.opsForValue().increment(key); // Redis 값 1 증가
        stringRedisTemplate.opsForSet().add(DIRTY_MEETINGS_KEY, String.valueOf(meetingId)); // Redis Set 자료구조에 meetingId 값 저장
        // Set 자료 구조는 Scheduled가 자동으로 비워줘서 TTL 설정 안함
        return incrementedCount;
    }

    // 조회수 컬럼만 조회
    public Long getSeeCountValue(Long meetingId){
        return meetingRepository.findSeeCountByMeetingId(meetingId)
                .orElseThrow(() -> new ResourceAlreadyExistsException("존재하지 않는 모임입니다."));
    }

    public void deleteCacheMeeting(Long meetingId) {
        Boolean isMeetingDeleted = redisTemplate.delete(MEETING_CACHE_PREFIX + meetingId);

        Boolean isViewDeleted = redisTemplate.delete(MEETING_VISIT_KEY + meetingId);

        Long isSetDeleted = stringRedisTemplate.opsForSet()
                .remove(DIRTY_MEETINGS_KEY, String.valueOf(meetingId));
    }
}
