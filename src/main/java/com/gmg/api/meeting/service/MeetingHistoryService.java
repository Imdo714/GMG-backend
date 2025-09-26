package com.gmg.api.meeting.service;

import com.gmg.api.Participant.domain.response.HistoryParticipant;
import com.gmg.api.meeting.domain.response.MeetingHistoryResponse;
import com.gmg.api.type.Category;

import java.time.LocalDate;
import java.time.LocalTime;

public interface MeetingHistoryService {
    MeetingHistoryResponse getMeetingHistoryList(Long memberId, LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category);

    HistoryParticipant historyParticipant(Long meetingId);
}
