package com.gmg.api.meeting.domain.response;

import com.gmg.api.type.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MeetingListResponse {

    private List<MeetingList> list;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class MeetingList {
        private Long meetingId;
        private String title;
        private LocalDate date;
        private LocalTime time;
        private Category category;
        private Integer personCount;
    }

    public static MeetingListResponse of(List<MeetingList> meetingList){
        return MeetingListResponse.builder()
                .list(meetingList)
                .build();
    }

}
