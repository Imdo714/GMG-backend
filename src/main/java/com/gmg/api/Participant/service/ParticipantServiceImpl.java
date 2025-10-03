package com.gmg.api.Participant.service;

import com.gmg.api.Participant.domain.response.ParticipantLogListResponse;
import com.gmg.api.Participant.domain.response.dto.HistoryDto;
import com.gmg.api.Participant.domain.response.dto.ParticipantLogDto;
import com.gmg.api.Participant.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;

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

}
