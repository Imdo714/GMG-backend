package com.gmg.api.Participant.repository.queryDsl;

import com.gmg.api.Participant.domain.entity.Participant;
import com.gmg.api.Participant.domain.response.dto.AcceptedParticipantDto;
import com.gmg.api.Participant.domain.response.dto.PendingParticipantDto;
import com.gmg.api.type.Status;

import java.util.List;
import java.util.Optional;

public interface ParticipantQueryDslRepository {

    boolean validateParticipantRequest(Long memberId, Long meetingId);
    List<PendingParticipantDto> getPendingParticipantListByMeetingId(Long meetingId);
    List<AcceptedParticipantDto> getAcceptedParticipantListByMeetingId(Long meetingId);

    Long getPersonCount(Long meetingId);
    long updateParticipantStatusToAccepted(Long meetingId, Long participantId, Status status);
}
