package com.gmg.api.meeting.repository;

import com.gmg.api.meeting.domain.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

}
