package com.gmg.api.Participant.domain.response.dto;

import com.gmg.api.type.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantLogDto {

    private Long meetingId;
    private String title;
    private Category category;
}
