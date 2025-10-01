package com.gmg.api.Participant.service;

import com.gmg.api.Participant.domain.request.ParticipantIdDto;
import com.gmg.api.Participant.domain.response.ParticipantListResponse;
import com.gmg.api.Participant.domain.response.ParticipantLogListResponse;
import com.gmg.api.Participant.domain.response.dto.HistoryDto;

import java.util.List;

public interface ParticipantService {

    ParticipantListResponse getParticipantList(Long meetingId);

    String participantAccepted(Long meetingId, Long memberId, ParticipantIdDto participantIdDto);

    String participantReject(Long meetingId, Long memberId, ParticipantIdDto participantIdDto);

    String participantCancel(Long meetingId, Long memberId, ParticipantIdDto participantIdDto);

    ParticipantLogListResponse getParticipantLogList(Long memberId);

    List<HistoryDto> historyParticipantReview(Long meetingId);
}
