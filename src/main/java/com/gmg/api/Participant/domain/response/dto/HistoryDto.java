package com.gmg.api.Participant.domain.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HistoryDto {

    private Long memberId;
    private String memberProfile;
    private String memberName;
    private String review;

}
