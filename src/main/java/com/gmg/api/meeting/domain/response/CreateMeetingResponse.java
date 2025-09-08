package com.gmg.api.meeting.domain.response;

import com.gmg.api.meeting.domain.entity.Meeting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateMeetingResponse {

    private Long meetingId;

    public static CreateMeetingResponse of(Meeting meeting) {
        return CreateMeetingResponse.builder()
                .meetingId(meeting.getMeetingId())
                .build();
    }
}
