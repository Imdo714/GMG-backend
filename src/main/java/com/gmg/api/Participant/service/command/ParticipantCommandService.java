package com.gmg.api.Participant.service.command;

import com.gmg.api.Participant.domain.request.ParticipantIdDto;

public interface ParticipantCommandService {
    String participantRequest(Long memberId, Long meetingId);
    String participantAccepted(Long meetingId, Long memberId, ParticipantIdDto participantIdDto);
    String participantReject(Long meetingId, Long memberId, ParticipantIdDto participantIdDto);
    String participantCancel(Long meetingId, Long memberId, ParticipantIdDto participantIdDto);

}
