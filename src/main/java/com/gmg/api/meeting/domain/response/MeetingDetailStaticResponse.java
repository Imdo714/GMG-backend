package com.gmg.api.meeting.domain.response;

import com.gmg.api.type.Category;
import com.gmg.api.meeting.domain.entity.Meeting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingDetailStaticResponse {

    private MeetingDetail meeting;

    @Getter
    @Builder
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

    public static MeetingDetailStaticResponse of(Meeting meeting){
        return MeetingDetailStaticResponse.builder()
                .meeting(MeetingDetail.builder()
                        .meetingId(meeting.getMeetingId())
                        .createMemberId(meeting.getMember().getMemberId())
                        .title(meeting.getTitle())
                        .content(meeting.getContent())
                        .category(meeting.getCategory())
                        .address(meeting.getAddress())
                        .addressDetail(meeting.getAddressDetail())
                        .personCount(meeting.getPersonCount())
                        .date(meeting.getDate())
                        .time(meeting.getTime())
                        .build())
                .build();
    }

    public static MeetingDetailStaticResponse of(MeetingDetailStaticResponse.MeetingDetail meeting){
        return MeetingDetailStaticResponse.builder()
                .meeting(MeetingDetail.builder()
                        .meetingId(meeting.meetingId)
                        .createMemberId(meeting.createMemberId)
                        .title(meeting.title)
                        .content(meeting.content)
                        .category(meeting.category)
                        .address(meeting.address)
                        .addressDetail(meeting.addressDetail)
                        .personCount(meeting.personCount)
                        .date(meeting.date)
                        .time(meeting.time)
                        .build())
                .build();
    }
}
