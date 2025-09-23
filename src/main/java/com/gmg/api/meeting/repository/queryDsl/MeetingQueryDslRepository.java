package com.gmg.api.meeting.repository.queryDsl;

import com.gmg.api.meeting.domain.response.MeetingDetailStaticResponse;
import com.gmg.api.meeting.domain.response.MeetingListResponse;
import com.gmg.api.type.Category;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MeetingQueryDslRepository {

    List<MeetingListResponse.MeetingList> getMeetingList(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category);

    boolean existsByMeetingIdAndMember_MemberId(Long meetingId, Long memberId);

    Optional<MeetingDetailStaticResponse.MeetingDetail> meetingDetailStatic(Long meetingId);
}
