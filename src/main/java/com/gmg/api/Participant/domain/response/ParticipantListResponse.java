package com.gmg.api.Participant.domain.response;

import com.gmg.api.Participant.domain.response.dto.AcceptedParticipantDto;
import com.gmg.api.Participant.domain.response.dto.PendingParticipantDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ParticipantListResponse {

    private List<PendingParticipantDto> pendingParticipantList;
    private List<AcceptedParticipantDto> acceptedParticipantList;

    public static ParticipantListResponse of(List<PendingParticipantDto> pendingParticipantDto, List<AcceptedParticipantDto> acceptedParticipantDto){
        return ParticipantListResponse.builder()
                .pendingParticipantList(pendingParticipantDto)
                .acceptedParticipantList(acceptedParticipantDto)
                .build();
    }
}
