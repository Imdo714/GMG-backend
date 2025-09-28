package com.gmg.api.Participant.domain.response;

import com.gmg.api.Participant.domain.response.dto.ParticipantLogDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class ParticipantLogListResponse {

    private List<ParticipantLogDto> logList;
    private Map<String, Long> stats;
    private int participantCount;
}
