package com.gmg.api.meeting.repository.queryDsl;

import com.gmg.api.meeting.domain.response.dto.MeetingValidationContext;
import com.gmg.api.meeting.domain.response.MeetingDetailStaticResponse;
import com.gmg.api.meeting.domain.response.MeetingHistoryResponse;
import com.gmg.api.meeting.domain.response.MeetingListResponse;
import com.gmg.api.type.Category;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MeetingQueryDslRepository {

    List<MeetingListResponse.MeetingListInfoDto2> getMeetingList2(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category);

    List<MeetingListResponse.MeetingListInfoDto> getMeetingList(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category);

    Map<Long, Long> getCountMap(List<MeetingListResponse.MeetingListInfoDto> meetings);

    boolean existsByMeetingIdAndMember_MemberId(Long meetingId, Long memberId);

    Optional<MeetingDetailStaticResponse.MeetingDetail> meetingDetailStatic(Long meetingId);

    void updateSeeCount(Map<Long, Integer> meetingViewCounts);

    List<MeetingHistoryResponse.MeetingHistoryList> getMeetingHistoryList(Long memberId, LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category);

    MeetingValidationContext validateParticipantRequest(Long memberId, Long meetingId);
}
