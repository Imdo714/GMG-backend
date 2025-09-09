package com.gmg.api.meeting.domain.response;

import com.gmg.api.meeting.domain.entity.Category;
import com.gmg.api.meeting.domain.entity.Meeting;
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
public class MeetingListResponse {

    private List<MeetingList> list;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class MeetingList {
        private String title;
        private LocalDate date;
        private LocalTime time;
        private Category category;
        private Integer personCount;
    }

    public static MeetingListResponse of(List<Meeting> meetingList){
        return MeetingListResponse.builder()
                .list(meetingList.stream()
                        .map(
                            m -> MeetingList.builder()
                                    .title(m.getTitle())
                                    .date(m.getDate())
                                    .time(m.getTime())
                                    .category(m.getCategory())
                                    .personCount(m.getPersonCount())
                                    .build()
                        ).collect(Collectors.toList())
                ).build();
    }

}
