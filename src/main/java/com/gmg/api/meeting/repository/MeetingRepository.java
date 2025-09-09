package com.gmg.api.meeting.repository;

import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.repository.queryDsl.MeetingQueryDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long>, MeetingQueryDslRepository {

}
