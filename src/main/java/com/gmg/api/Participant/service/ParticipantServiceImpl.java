package com.gmg.api.Participant.service;

import com.gmg.api.Participant.domain.request.ParticipantIdDto;
import com.gmg.api.Participant.domain.response.ParticipantListResponse;
import com.gmg.api.Participant.domain.response.ParticipantLogListResponse;
import com.gmg.api.Participant.domain.response.dto.AcceptedParticipantDto;
import com.gmg.api.Participant.domain.response.dto.HistoryDto;
import com.gmg.api.Participant.domain.response.dto.ParticipantLogDto;
import com.gmg.api.Participant.domain.response.dto.PendingParticipantDto;
import com.gmg.api.Participant.repository.ParticipantRepository;
import com.gmg.api.meeting.service.MeetingService;
import com.gmg.global.exception.handelException.MatchMissException;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final MeetingService meetingService;

    @Override
    public ParticipantListResponse getParticipantList(Long meetingId) {
        return ParticipantListResponse.of(getPendingParticipantListByMeetingId(meetingId), getAcceptedParticipantListByMeetingId(meetingId));
    }

    @Override
    public ParticipantLogListResponse getParticipantLogList(Long memberId) {
        // 1. 참여했던 모임 ID, 제목
        List<ParticipantLogDto> participantLogList = participantRepository.getParticipantLogList(memberId);

        Map<String, Long> stats = participantLogList.stream()
                .collect(Collectors.groupingBy(
                        meeting -> meeting.getCategory().name(),
                        Collectors.counting() // 각 그룹의 개수를 센다
                ));

        return ParticipantLogListResponse.builder()
                .logList(participantLogList)
                .stats(stats)
                .participantCount(participantLogList.size())
                .build();
    }

    @Override
    public List<HistoryDto> historyParticipantReview(Long meetingId) {
        return participantRepository.historyParticipantReview(meetingId);
    }

    // 신청 대기 리스트 반환 메서드
    private List<PendingParticipantDto> getPendingParticipantListByMeetingId(Long meetingId) {
        return participantRepository.getPendingParticipantListByMeetingId(meetingId);
    }

    // 신청 승인 리스트 반환 메서드
    private List<AcceptedParticipantDto> getAcceptedParticipantListByMeetingId(Long meetingId) {
        return participantRepository.getAcceptedParticipantListByMeetingId(meetingId);
    }

}
