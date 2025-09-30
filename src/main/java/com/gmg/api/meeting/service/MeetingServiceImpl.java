package com.gmg.api.meeting.service;

import com.gmg.api.Participant.domain.entity.Participant;
import com.gmg.api.Participant.repository.ParticipantRepository;
import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.domain.request.CreateMeetingDto;
import com.gmg.api.meeting.domain.response.CreateMeetingResponse;
import com.gmg.api.meeting.domain.response.MeetingDetailStaticResponse;
import com.gmg.api.meeting.domain.response.MeetingListResponse;
import com.gmg.api.meeting.domain.response.SeeCountResponse;
import com.gmg.api.meeting.repository.MeetingRepository;
import com.gmg.api.meeting.service.redis.MeetingRedisService;
import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.service.MemberService;
import com.gmg.api.type.Category;
import com.gmg.global.exception.handelException.MatchMissException;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;

    @Override
    public Meeting getMeetingById(Long meetingId) {
        return meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new ResourceAlreadyExistsException("존재하지 않는 모임입니다."));
    }

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

    @Override
    public Long getMakeMeetingOwner(Long meetingId) {
        return meetingRepository.getMakeMeetingOwner(meetingId)
                .orElseThrow(() -> new ResourceAlreadyExistsException("존재하지 않는 모임입니다."));
    }

}
