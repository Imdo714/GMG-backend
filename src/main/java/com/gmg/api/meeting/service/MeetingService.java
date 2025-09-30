package com.gmg.api.meeting.service;

import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.domain.request.CreateMeetingDto;
import com.gmg.api.meeting.domain.response.CreateMeetingResponse;
import com.gmg.api.meeting.domain.response.MeetingDetailStaticResponse;
import com.gmg.api.meeting.domain.response.MeetingListResponse;
import com.gmg.api.meeting.domain.response.SeeCountResponse;
import com.gmg.api.type.Category;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

public interface MeetingService {

    Meeting getMeetingById(Long meetingId);
    Meeting getReferenceMeetingById(Long meetingId);
    boolean validateMeetingOwner(Long meetingId, Long memberId);
    Long getMakeMeetingOwner(Long meetingId);
}
