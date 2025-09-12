package com.gmg.api.meeting.domain.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeeCountResponse {

    private Long meetingId;
    private Long seeCount;

    public static SeeCountResponse of(Long meetingId, Long seeCount){
        return SeeCountResponse.builder()
                .meetingId(meetingId)
                .seeCount(seeCount)
                .build();
    }
}
