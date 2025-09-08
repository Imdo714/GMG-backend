package com.gmg.api.meeting.service;

import com.gmg.api.meeting.domain.request.CreateMeetingDto;
import com.gmg.api.meeting.domain.response.CreateMeetingResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MeetingService {

    CreateMeetingResponse createMeeting(Long userId, CreateMeetingDto createMeetingDto, MultipartFile image);
}
