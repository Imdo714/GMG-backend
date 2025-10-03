package com.gmg.api.meeting.service;

import com.gmg.api.meeting.domain.entity.Meeting;

public interface MeetingService {

    Meeting getReferenceMeetingById(Long meetingId);
    boolean validateMeetingOwner(Long meetingId, Long memberId);
}
