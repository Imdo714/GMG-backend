package com.gmg.api.Participant.service.command;

import com.gmg.api.Participant.domain.request.ParticipantIdDto;

public interface ParticipantCommandService {
    String participantRequest(Long memberId, Long meetingId);
    String updateParticipantAccepted(Long meetingId, Long memberId, ParticipantIdDto participantIdDto);
    String updateParticipantReject(Long meetingId, Long memberId, ParticipantIdDto participantIdDto);
    String updateParticipantCancel(Long meetingId, Long memberId, ParticipantIdDto participantIdDto);

}
