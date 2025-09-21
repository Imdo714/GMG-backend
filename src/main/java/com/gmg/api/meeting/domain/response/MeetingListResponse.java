package com.gmg.api.meeting.domain.response;

import com.gmg.api.type.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class MeetingListResponse {

    private List<MeetingList> list;
    private boolean hasNext;

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
        private Integer seeCount;
        private Long acceptedCount;
    }


    // 정적 팩토리 및 메서드 오버로딩 사용
    public static MeetingListResponse of(List<MeetingList> meetingList, Map<Long, Long> acceptedCountMap, boolean hasNext) {
        return MeetingListResponse.builder()
                .list(meetingList.stream()
                        .map(meeting -> MeetingList.builder()
                                .meetingId(meeting.getMeetingId())
                                .title(meeting.getTitle())
                                .date(meeting.getDate())
                                .time(meeting.getTime())
                                .category(meeting.getCategory())
                                .personCount(meeting.getPersonCount())
                                .seeCount(meeting.getSeeCount())
                                .acceptedCount(acceptedCountMap.getOrDefault(meeting.getMeetingId(), 0L))
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .hasNext(hasNext)
                .build();
    }
}
