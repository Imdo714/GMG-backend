package com.gmg.api.meeting.repository;

import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.repository.queryDsl.MeetingQueryDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long>, MeetingQueryDslRepository {

    Optional<Meeting> findByMeetingId(Long meetingId);

}
