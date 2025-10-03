package com.gmg.api.Participant.domain.response.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AcceptedParticipantDto {
    private Long participantId;
    private Long memberId;
    private String memberProfile;
    private String memberName;

    public static AcceptedParticipantDto toAcceptedDto(ParticipantDto p) {
        return AcceptedParticipantDto.builder()
                .participantId(p.getParticipantId())
                .memberId(p.getMemberId())
                .memberProfile(p.getMemberProfile())
                .memberName(p.getMemberName())
                .build();
    }
}
