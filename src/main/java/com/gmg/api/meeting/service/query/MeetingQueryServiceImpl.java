package com.gmg.api.meeting.service.query;

import com.gmg.api.Participant.domain.response.HistoryParticipant;
import com.gmg.api.Participant.domain.response.dto.HistoryDto;
import com.gmg.api.Participant.service.ParticipantService;
import com.gmg.api.meeting.domain.response.MeetingDetailStaticResponse;
import com.gmg.api.meeting.domain.response.MeetingHistoryResponse;
import com.gmg.api.meeting.domain.response.MeetingListResponse;
import com.gmg.api.meeting.repository.MeetingRepository;
import com.gmg.api.meeting.service.redis.MeetingRedisService;
import com.gmg.api.type.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingQueryServiceImpl implements MeetingQueryService {

    private final MeetingRepository meetingRepository;
    private final MeetingRedisService meetingRedisService;
    private final ParticipantService participantService;

    @Override
    public MeetingListResponse getMeetingList(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category) {
        List<MeetingListResponse.MeetingListInfoDto> meetings = meetingRepository.getMeetingListOptimized(lastMeetingDate, lastMeetingTime, lastMeetingId, size, category);

        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        List<MeetingListResponse.MeetingListDto> meetingDtos = MeetingListResponse.toDtoList(meetings, nowDate, nowTime);

        boolean hasNext = meetings.size() > size;

        return MeetingListResponse.builder()
                .list(meetingDtos)
                .hasNext(hasNext)
                .build();
    }

    /**
     * 위 방식 단일쿼리 (서브쿼리)
     *      장점 : 한번의 DB 호출로 인해 네트워크 비용이 적어 응답 시간이 빠름
     *      단점 : 옵티마이저의 판단에 맡겨야 해서 인덱스를 잘 활용하지 못하여 잠재적 위험이 있음
     *
     * 아래 방식 2번의 쿼리
     *      장점 : 너무 안전함 첫 번째 쿼리에서 인덱스를 타서 빠르고, 두 번째 쿼리는 항상 소수의 ID만 조회
     *      단점 : DB 커넥션 부담이 있다. 부하가 높을 때 각 요청이 커넥션을 두 개씩 점유해서 커넥션 풀 설정에 신경 써야 함
     * */

//    @Override
//    public MeetingListResponse getMeetingListSubQuery(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category) {
//        List<Long> meetings = meetingRepository.getMeetingListId(lastMeetingDate, lastMeetingTime, lastMeetingId, size, category);
//        boolean hasNext = meetings.size() > size;
//        List<MeetingListResponse.MeetingListInfoDto2> meetingsRes = meetingRepository.getMeetingListInfo(meetings);
//
//        LocalDate nowDate = LocalDate.now();
//        LocalTime nowTime = LocalTime.now();
//
//        List<MeetingListResponse.MeetingListDto> meetingDtos = MeetingListResponse.toDtoList(meetingsRes, nowDate, nowTime);
//
//        return MeetingListResponse.builder()
//                .list(meetingDtos)
//                .hasNext(hasNext)
//                .build();
//    }

    @Override // Redis 캐싱에서 Meeting 본문 값 가져오는 메서드 (global Cache)
    public MeetingDetailStaticResponse getMeetingDetail(Long meetingId) {
        // 캐시에서 조회
        MeetingDetailStaticResponse.MeetingDetail meeting = meetingRedisService.getFromCache(meetingId);

        // 캐시에 없으면 DB 조회 후 캐싱
        if (meeting == null) {
            meeting = meetingRedisService.getFromDbAndCache(meetingId);
        }

        boolean isClosed = LocalDateTime.of(meeting.getDate(), meeting.getTime()).isBefore(LocalDateTime.now());
        return MeetingDetailStaticResponse.of(meeting, isClosed);
    }

    @Override
    public MeetingHistoryResponse getMeetingHistoryList(Long memberId, LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category) {
        List<MeetingHistoryResponse.MeetingHistoryList> meetingHistoryList = meetingRepository.getMeetingHistoryList(memberId, lastMeetingDate, lastMeetingTime, lastMeetingId, size, category);
        boolean hasNext = meetingHistoryList.size() > size;

        return MeetingHistoryResponse.of(meetingHistoryList, hasNext);
    }

    @Override
    public HistoryParticipant historyParticipant(Long meetingId) {
        List<HistoryDto> acceptedParticipantDtos = participantService.historyParticipantReview(meetingId);
        return HistoryParticipant.builder()
                .list(acceptedParticipantDtos)
                .build();
    }

}
