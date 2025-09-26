package com.gmg.api.Participant.domain.response;

import com.gmg.api.Participant.domain.response.dto.HistoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class HistoryParticipant {
    private List<HistoryDto> list;

    public static HistoryParticipant of(List<HistoryDto> acceptedParticipant){
        return HistoryParticipant.builder()
                .list(acceptedParticipant)
                .build();
    }
}
