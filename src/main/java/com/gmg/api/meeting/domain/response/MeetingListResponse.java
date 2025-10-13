package com.gmg.api.meeting.domain.response;

import com.gmg.api.type.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class MeetingListResponse {

    private List<MeetingListDto> list;
    private boolean hasNext;

    public static MeetingListResponse.MeetingListDto toDto(MeetingListInfoDto meeting, Map<Long, Long> participantCountMap, LocalDate nowDate, LocalTime nowTime) {
        return MeetingListResponse.MeetingListDto.builder()
                .meetingId(meeting.getMeetingId())
                .title(meeting.getTitle())
                .date(meeting.getDate())
                .time(meeting.getTime())
                .category(meeting.getCategory())
                .personCount(meeting.getPersonCount())
                .seeCount(meeting.getSeeCount())
                .acceptedCount(participantCountMap.getOrDefault(meeting.getMeetingId(), 0L))
                .isClosed(isClosed(meeting.getDate(), meeting.getTime(), nowDate, nowTime))
                .build();
    }

    private static boolean isClosed(LocalDate meetingDate, LocalTime meetingTime, LocalDate nowDate, LocalTime nowTime) {
        return meetingDate.isBefore(nowDate)
                || (meetingDate.isEqual(nowDate) && meetingTime.isBefore(nowTime));
    }

    @Getter
    @Builder
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

    @Getter
    @AllArgsConstructor
    public static class MeetingListInfoDto {

        private Long meetingId;
        private String title;
        private LocalDate date;
        private LocalTime time;
        private Category category;
        private Integer personCount;
        private Integer seeCount;
    }
}
