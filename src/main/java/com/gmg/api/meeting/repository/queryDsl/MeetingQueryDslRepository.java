package com.gmg.api.meeting.repository.queryDsl;

import com.gmg.api.meeting.domain.response.MeetingListResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MeetingQueryDslRepository {

    List<MeetingListResponse.MeetingList> getMeetingList(LocalDate lastMeetingDate, LocalTime lastMeetingTime, int size);

    boolean existsByMeetingIdAndMember_MemberId(Long meetingId, Long memberId);
}
