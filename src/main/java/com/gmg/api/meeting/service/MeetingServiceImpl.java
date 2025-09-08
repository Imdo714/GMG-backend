package com.gmg.api.meeting.service;

import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.domain.request.CreateMeetingDto;
import com.gmg.api.meeting.domain.response.CreateMeetingResponse;
import com.gmg.api.meeting.repository.MeetingRepository;
import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

}
