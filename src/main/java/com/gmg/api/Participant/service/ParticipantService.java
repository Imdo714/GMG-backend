package com.gmg.api.Participant.service;

import com.gmg.api.Participant.domain.response.ParticipantListResponse;

public interface ParticipantService {

    String participantRequest(Long memberId, Long meetingId);

    ParticipantListResponse getParticipantList(Long meetingId);
}
