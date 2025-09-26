package com.gmg.api.meeting.service;

import com.gmg.api.Participant.domain.response.HistoryParticipant;
import com.gmg.api.Participant.repository.ParticipantRepository;
import com.gmg.api.meeting.domain.response.MeetingHistoryResponse;
import com.gmg.api.Participant.domain.response.dto.HistoryDto;
import com.gmg.api.meeting.repository.MeetingRepository;
import com.gmg.api.type.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeetingHistoryServiceImpl implements MeetingHistoryService {

    private final MeetingRepository meetingRepository;
    private final ParticipantRepository participantRepository;

    @Override
    public MeetingHistoryResponse getMeetingHistoryList(Long memberId, LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category) {
        List<MeetingHistoryResponse.MeetingHistoryList> meetingHistoryList = meetingRepository.getMeetingHistoryList(memberId, lastMeetingDate, lastMeetingTime, lastMeetingId, size, category);
        boolean hasNext = meetingHistoryList.size() > size;

        return MeetingHistoryResponse.of(meetingHistoryList, hasNext);
    }

    @Override
    public HistoryParticipant historyParticipant(Long meetingId) {
        List<HistoryDto> acceptedParticipantDtos = participantRepository.historyParticipantReview(meetingId);

        return HistoryParticipant.of(acceptedParticipantDtos);
    }

}
