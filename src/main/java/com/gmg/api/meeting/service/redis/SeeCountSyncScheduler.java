package com.gmg.api.meeting.service.redis;

import com.gmg.api.meeting.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeeCountSyncScheduler {

    private final RedisTemplate<String, String> stringRedisTemplate;
    private final MeetingRepository meetingRepository;

    private static final String MEETING_VISIT_KEY = "meeting:visit:";
    private static final String DIRTY_MEETINGS_KEY = "dirty:meetings";

    // 10분마다 실행
//    @Scheduled(cron = "*/30 * * * * *")
    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void syncViewCountsToDb() {
        // 1. Set에 저장된 모든 변경된 meetingId를 가져오고, 해당 Set을 비웁니다.
        // pop 명령어를 사용하면 원자적으로 값을 가져오고 삭제할 수 있음
        List<String> dirtyMeetingIds = stringRedisTemplate.opsForSet().pop(DIRTY_MEETINGS_KEY, 1000);
        log.info("dirtyMeetingIds = {}", dirtyMeetingIds);

        if (dirtyMeetingIds.isEmpty()) {
            return;
        }
        log.info("동기화 대상 ID 개수: {}개", dirtyMeetingIds.size());

        Map<Long, Integer> meetingViewCounts = new HashMap<>();
        for (String meetingIdStr : dirtyMeetingIds) {
            String viewCountKey = MEETING_VISIT_KEY + meetingIdStr;
            String viewCountStr = stringRedisTemplate.opsForValue().get(viewCountKey);

            if (viewCountStr != null) {
                long meetingId = Long.parseLong(meetingIdStr);
                int viewCount = Integer.parseInt(viewCountStr);
                meetingViewCounts.put(meetingId, viewCount);
            }
        }
        meetingRepository.updateSeeCount(meetingViewCounts);
    }

}
