package com.gmg.api.meeting.repository.queryDsl;

import com.gmg.api.meeting.domain.response.MeetingListResponse;
import com.gmg.api.type.Category;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MeetingQueryDslRepository {

    List<MeetingListResponse.MeetingList> getMeetingList(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category);

    boolean existsByMeetingIdAndMember_MemberId(Long meetingId, Long memberId);
}
