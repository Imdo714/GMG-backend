package com.gmg.api.meeting.service;

import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.domain.request.CreateMeetingDto;
import com.gmg.api.meeting.domain.response.CreateMeetingResponse;
import com.gmg.api.meeting.domain.response.MeetingDetailStaticResponse;
import com.gmg.api.meeting.domain.response.MeetingListResponse;
import com.gmg.api.meeting.repository.MeetingRepository;
import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.service.MemberService;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final MemberService memberService;

    @Override
    public CreateMeetingResponse createMeeting(Long memberId, CreateMeetingDto createMeetingDto, MultipartFile image) {
        Member member = memberService.getByuserIdMember(memberId);
        // TODO: 이미지 S3에 저장, S3 구축 되면 비동기적으로 저장할 예정

        Meeting meeting = Meeting.of(member, createMeetingDto);
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
        Meeting meeting = getByMeetingId(meetingId);
        return MeetingDetailStaticResponse.of(meeting);
    }

    private Meeting getByMeetingId(Long meetingId) {
        return meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new ResourceAlreadyExistsException("존재하지 않는 모임입니다."));
    }

}
