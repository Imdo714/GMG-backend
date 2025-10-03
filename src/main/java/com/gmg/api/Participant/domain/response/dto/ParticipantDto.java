package com.gmg.api.Participant.domain.response.dto;

import com.gmg.api.type.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantDto {
    private Long participantId;
    private Long memberId;
    private String memberProfile;
    private String memberName;
    private Status status;
}
