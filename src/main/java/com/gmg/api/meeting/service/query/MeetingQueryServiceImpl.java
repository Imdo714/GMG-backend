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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
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
        // Size + 1은 마지막 페이지인지 확인 하기 위해
        List<MeetingListResponse.MeetingList> meetingList = getMeetingListFetch(lastMeetingDate, lastMeetingTime, lastMeetingId, size + 1, category);
        boolean hasNext = meetingList.size() > size;

        markClosedMeetings(meetingList); // 날짜 시간 기준으로 마감 체크
        Map<Long, Long> acceptedCountMap = getAcceptedCountMapByMeetings(meetingList); // 승인된 인원 분리
        return MeetingListResponse.of(meetingList, acceptedCountMap, hasNext);
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

    @Override //
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

    // Meeting 리스트를 가져오는 메서드
    private List<MeetingListResponse.MeetingList> getMeetingListFetch(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category) {
        return meetingRepository.getMeetingList(lastMeetingDate, lastMeetingTime, lastMeetingId, size, category);
    }

    // Meeting 리스트들 마감된 모임들만 체크해서 마감 처리
    private static void markClosedMeetings(List<MeetingListResponse.MeetingList> meetingList) {
        LocalDateTime now = LocalDateTime.now();
        for (MeetingListResponse.MeetingList meeting : meetingList) {
            LocalDateTime meetingDateTime = LocalDateTime.of(meeting.getDate(), meeting.getTime());
            if (meetingDateTime.isBefore(now)) { // 모임 날짜가 현재 날짜보다 이전이면
                meeting.updateStatus(true); // 마감됨
            } else {
                meeting.updateStatus(false); // 진행중
            }
        }
    }

    // meetingList 를 받아 안에있는 meetingId를 뽑아서 meetingId별 승인 인원이 몇명인지 반환
    private Map<Long, Long> getAcceptedCountMapByMeetings(List<MeetingListResponse.MeetingList> meetingList) {
        return participantService.getAcceptedCountsByMeetingIds(meetingList);
    }

}
