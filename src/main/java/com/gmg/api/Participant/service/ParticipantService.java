package com.gmg.api.Participant.service;

import com.gmg.api.Participant.domain.response.ParticipantListResponse;
import com.gmg.api.Participant.domain.response.ParticipantLogListResponse;
import com.gmg.api.Participant.domain.response.dto.HistoryDto;

import java.util.List;

public interface ParticipantService {

    ParticipantListResponse getParticipantList(Long meetingId);

    ParticipantLogListResponse getParticipantLogList(Long memberId);

    List<HistoryDto> historyParticipantReview(Long meetingId);
}
