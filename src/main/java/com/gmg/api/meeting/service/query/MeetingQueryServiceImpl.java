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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingQueryServiceImpl implements MeetingQueryService {

    private final MeetingRepository meetingRepository;
    private final MeetingRedisService meetingRedisService;
    private final ParticipantService participantService;

    // CQRS 패턴으로 Query 쓰기 영역
    // TODO : MeetingQueryServiceImpl 클래스 전체 튜닝 DB 쿼리 최대한 줄이면서 레이턴시 줄이기, SOLID 최대한 지키기

    @Override
    public MeetingListResponse getMeetingList(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category) {
        List<MeetingListResponse.MeetingListInfoDto> meetings = meetingRepository.getMeetingList(lastMeetingDate, lastMeetingTime, lastMeetingId, size, category);
        Map<Long, Long> participantCountMap = meetingRepository.getCountMap(meetings);
        boolean hasNext = meetings.size() > size;

        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        List<MeetingListResponse.MeetingListDto> meetingDtos = meetings.stream()
                .map(meeting -> MeetingListResponse.toDto(meeting, participantCountMap, nowDate, nowTime))
                .collect(Collectors.toList());

        return MeetingListResponse.builder()
                .list(meetingDtos)
                .hasNext(hasNext)
                .build();
    }

    @Override
    public MeetingListResponse getMeetingList2(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category) {
        List<MeetingListResponse.MeetingListInfoDto2> meetings = meetingRepository.getMeetingList2(lastMeetingDate, lastMeetingTime, lastMeetingId, size, category);
        boolean hasNext = meetings.size() > size;

        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        List<MeetingListResponse.MeetingListDto> meetingDtos = MeetingListResponse.toDtoList(meetings, nowDate, nowTime);

        return MeetingListResponse.builder()
                .list(meetingDtos)
                .hasNext(hasNext)
                .build();
    }

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
