package com.gmg.api.meeting.service;

import com.gmg.api.Participant.domain.entity.Participant;
import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.domain.request.CreateMeetingDto;
import com.gmg.api.meeting.domain.response.CreateMeetingResponse;
import com.gmg.api.meeting.domain.response.MeetingDetailStaticResponse;
import com.gmg.api.meeting.domain.response.MeetingListResponse;
import com.gmg.api.meeting.domain.response.SeeCountResponse;
import com.gmg.api.meeting.repository.MeetingRepository;
import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.service.MemberService;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private static final String MEETING_VISIT_KEY = "meeting:visit:";

    private final MeetingRepository meetingRepository;
    private final MemberService memberService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public CreateMeetingResponse createMeeting(Long memberId, CreateMeetingDto createMeetingDto, MultipartFile image) {
        Member member = memberService.getMemberById(memberId);
        // TODO: 이미지 S3에 저장, S3 구축 되면 비동기적으로 저장할 예정

        Meeting meeting = Meeting.of(member, createMeetingDto); // 모임 생성
        Participant participant = Participant.ofLeader(member, meeting); // 모임 생성한 사람을 참가자에 넣음
        meeting.addParticipant(participant);

        return CreateMeetingResponse.of(meetingRepository.save(meeting));
    }

    @Override
    public MeetingListResponse getMeetingList(LocalDate lastMeetingDate, LocalTime lastMeetingTime, int size) {
        List<MeetingListResponse.MeetingList> meetingList = meetingRepository.getMeetingList(lastMeetingDate, lastMeetingTime, size);
        return MeetingListResponse.of(meetingList);
    }

    @Override
    @Cacheable(cacheNames = "meetingDetailCache", key = "#meetingId")
    public MeetingDetailStaticResponse getMeetingDetail(Long meetingId) {
        Meeting meeting = getMeetingById(meetingId);
        return MeetingDetailStaticResponse.of(meeting);
    }

    @Override
    @Transactional
    public SeeCountResponse updateMeetingViews(Long meetingId) {
        String key = MEETING_VISIT_KEY + meetingId;

        ensureMeetingViewCountInitialized(meetingId, key); // Redis 에 키값 없으면 설정
        Long updatedCount = redisTemplate.opsForValue().increment(key); // Redis 1증가하고 값 반환

        return SeeCountResponse.of(meetingId, updatedCount);
    }

    @Override
    public Meeting getMeetingById(Long meetingId) {
        return meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new ResourceAlreadyExistsException("존재하지 않는 모임입니다."));
    }

    private void ensureMeetingViewCountInitialized(Long meetingId, String key) {
        if (redisTemplate.opsForValue().get(key) == null) {
            Meeting meeting = getMeetingById(meetingId);
            redisTemplate.opsForValue().set(key, meeting.getPersonCount());
        }
    }



}
