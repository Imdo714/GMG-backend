package com.gmg.api.meeting.service.command;

import com.gmg.api.meeting.domain.request.CreateMeetingDto;
import com.gmg.api.meeting.domain.response.CreateMeetingResponse;
import com.gmg.api.meeting.domain.response.SeeCountResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MeetingCommandService {
    CreateMeetingResponse createMeeting(Long memberId, CreateMeetingDto createMeetingDto, MultipartFile image);
    String deleteMeeting(Long meetingId, Long memberId);
    SeeCountResponse increaseViews(Long meetingId);
}
