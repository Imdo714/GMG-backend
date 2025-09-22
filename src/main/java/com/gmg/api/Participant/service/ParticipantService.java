package com.gmg.api.Participant.service;

import com.gmg.api.Participant.domain.request.ParticipantIdDto;
import com.gmg.api.Participant.domain.response.ParticipantListResponse;

public interface ParticipantService {

    String participantRequest(Long memberId, Long meetingId);

    ParticipantListResponse getParticipantList(Long meetingId);

    String participantAccepted(Long meetingId, Long memberId, ParticipantIdDto participantIdDto);

    String participantReject(Long meetingId, Long memberId, ParticipantIdDto participantIdDto);

    String participantCancel(Long meetingId, Long memberId, ParticipantIdDto participantIdDto);
}
