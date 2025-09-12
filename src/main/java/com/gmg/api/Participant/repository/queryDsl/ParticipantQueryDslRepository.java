package com.gmg.api.Participant.repository.queryDsl;

import com.gmg.api.Participant.domain.response.dto.AcceptedParticipantDto;
import com.gmg.api.Participant.domain.response.dto.PendingParticipantDto;

import java.util.List;

public interface ParticipantQueryDslRepository {

    boolean validateParticipantRequest(Long memberId, Long meetingId);

    List<PendingParticipantDto> getPendingParticipantListByMeetingId(Long meetingId);

    List<AcceptedParticipantDto> getAcceptedParticipantListByMeetingId(Long meetingId);
}
