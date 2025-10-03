package com.gmg.api.meeting.service;

import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.repository.MeetingRepository;
import com.gmg.global.exception.handelException.MatchMissException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;

    @Override
    public Meeting getReferenceMeetingById(Long meetingId) {
        try {
            return meetingRepository.getReferenceById(meetingId);
        } catch (EntityNotFoundException e) {
            throw new MatchMissException("존재하지 않는 모임입니다.");
        }
    }

    @Override // 방장 여부 판단 boolean 반환 값
    public boolean validateMeetingOwner(Long meetingId, Long memberId) {
        return meetingRepository.existsByMeetingIdAndMember_MemberId(meetingId, memberId);
    }

}
