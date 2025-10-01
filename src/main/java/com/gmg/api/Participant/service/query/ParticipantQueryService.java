package com.gmg.api.Participant.service.query;

import com.gmg.api.Participant.domain.response.ParticipantListResponse;

public interface ParticipantQueryService {
    ParticipantListResponse getParticipantList(Long meetingId);
}
