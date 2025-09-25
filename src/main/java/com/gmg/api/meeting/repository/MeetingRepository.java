package com.gmg.api.meeting.repository;

import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.repository.queryDsl.MeetingQueryDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Map;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long>, MeetingQueryDslRepository {

    Optional<Meeting> findByMeetingId(Long meetingId);

    @Query("SELECT m.seeCount FROM Meeting m WHERE m.meetingId = :meetingId")
    Optional<Long> findSeeCountByMeetingId(@Param("meetingId") Long meetingId);

    @Query("SELECT m.member.memberId FROM Meeting m WHERE m.meetingId = :meetingId")
    Optional<Long> getMakeMeetingOwner(@Param("meetingId") Long meetingId);
}
