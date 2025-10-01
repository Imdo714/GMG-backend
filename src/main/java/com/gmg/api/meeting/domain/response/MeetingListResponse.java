package com.gmg.api.meeting.domain.response;

import com.gmg.api.type.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MeetingListResponse {

    private List<MeetingListDto> list;
    private boolean hasNext;

    @Getter
    @AllArgsConstructor
    public static class MeetingListDto {

        private Long meetingId;
        private String title;
        private LocalDate date;
        private LocalTime time;
        private Category category;
        private Integer personCount;
        private Integer seeCount;
        private Long acceptedCount;
        private boolean isClosed;
    }
}
