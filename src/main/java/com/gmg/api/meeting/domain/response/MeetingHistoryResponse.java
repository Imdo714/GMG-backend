package com.gmg.api.meeting.domain.response;

import com.gmg.api.type.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class MeetingHistoryResponse {

    private List<MeetingHistoryList> list;
    private boolean hasNext;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class MeetingHistoryList {
        private Long meetingId;
        private String title;
        private LocalDate date;
        private LocalTime time;
        private Category category;
        private Integer seeCount;
    }

    public static MeetingHistoryResponse of(List<MeetingHistoryResponse.MeetingHistoryList> meetingHistoryList, boolean hasNext){
        return MeetingHistoryResponse.builder()
                .list(meetingHistoryList.stream()
                        .map(it -> MeetingHistoryList.builder()
                                .meetingId(it.meetingId)
                                .title(it.title)
                                .date(it.date)
                                .time(it.time)
                                .category(it.category)
                                .seeCount(it.seeCount)
                                .build()
                        ).collect(Collectors.toList())
                )
                .hasNext(hasNext)
                .build();
    }
}
