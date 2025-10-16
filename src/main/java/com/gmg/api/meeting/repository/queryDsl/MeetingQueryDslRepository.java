package com.gmg.api.meeting.repository.queryDsl;

import com.gmg.api.meeting.domain.response.MeetingDetailStaticResponse;
import com.gmg.api.meeting.domain.response.MeetingHistoryResponse;
import com.gmg.api.meeting.domain.response.MeetingListResponse;
import com.gmg.api.meeting.domain.response.dto.MeetingValidationContext;
import com.gmg.api.type.Category;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MeetingQueryDslRepository {

    List<Long> getMeetingListId(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category);

    List<MeetingListResponse.MeetingListInfoDto> getMeetingListInfo(List<Long> meetings);

    List<MeetingListResponse.MeetingListInfoDto> getMeetingListOptimized(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category);

    boolean existsByMeetingIdAndMember_MemberId(Long meetingId, Long memberId);

    Optional<MeetingDetailStaticResponse.MeetingDetail> meetingDetailStatic(Long meetingId);

    void updateSeeCount(Map<Long, Integer> meetingViewCounts);

    List<MeetingHistoryResponse.MeetingHistoryList> getMeetingHistoryList(Long memberId, LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category);

    MeetingValidationContext validateParticipantRequest(Long memberId, Long meetingId);
}
