package com.gmg.api.meeting.repository.queryDsl;

import com.gmg.api.meeting.domain.entity.Meeting;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MeetingQueryDslRepository {

    List<Meeting> getMeetingList(LocalDate lastMeetingDate, LocalTime lastMeetingTime, int size);
}
