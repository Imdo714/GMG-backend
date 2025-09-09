package com.gmg.api.meeting.service;

import com.gmg.api.meeting.domain.request.CreateMeetingDto;
import com.gmg.api.meeting.domain.response.CreateMeetingResponse;
import com.gmg.api.meeting.domain.response.MeetingListResponse;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

public interface MeetingService {

    CreateMeetingResponse createMeeting(Long userId, CreateMeetingDto createMeetingDto, MultipartFile image);

    MeetingListResponse getMeetingList(LocalDate lastMeetingDate, LocalTime lastMeetingTime, int size);
}
