package com.gmg.api.Participant.domain.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingApprovalCheckDto {

    private Integer personCount;
    private Long approvedCount;

    public boolean isFull() {
        return approvedCount >= personCount;
    }
}
