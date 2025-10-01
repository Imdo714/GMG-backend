package com.gmg.api.meeting.domain.response;

import com.gmg.api.type.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingDetailStaticResponse {

    private MeetingDetail meeting;
    private boolean isClosed;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MeetingDetail {
        private Long meetingId;
        private Long createMemberId;
        private String title;
        private String content;
        private Category category;
        private String address;
        private String addressDetail;
        private Integer personCount;
        private LocalDate date;
        private LocalTime time;
    }

    public static MeetingDetailStaticResponse of(MeetingDetailStaticResponse.MeetingDetail meeting, boolean isClosed){
        return MeetingDetailStaticResponse.builder()
                .meeting(meeting)
                .isClosed(isClosed)
                .build();
    }
}
