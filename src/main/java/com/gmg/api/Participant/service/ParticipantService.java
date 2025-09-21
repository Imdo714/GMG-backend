package com.gmg.api.Participant.service;

import com.gmg.api.Participant.domain.request.ParticipantAcceptedDto;
import com.gmg.api.Participant.domain.response.ParticipantListResponse;
import com.gmg.api.Participant.domain.response.dto.AcceptedParticipantDto;
import com.gmg.api.Participant.domain.response.dto.PendingParticipantDto;

import java.util.List;

public interface ParticipantService {

    String participantRequest(Long memberId, Long meetingId);

    ParticipantListResponse getParticipantList(Long meetingId);

    String participantAccepted(Long meetingId, Long memberId, ParticipantAcceptedDto participantAcceptedDto);

    String participantReject(Long meetingId, Long memberId, ParticipantAcceptedDto participantAcceptedDto);
}
