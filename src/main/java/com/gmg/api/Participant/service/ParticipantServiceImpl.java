package com.gmg.api.Participant.service;

import com.gmg.api.Participant.domain.entity.Participant;
import com.gmg.api.Participant.domain.response.ParticipantListResponse;
import com.gmg.api.Participant.domain.response.dto.AcceptedParticipantDto;
import com.gmg.api.Participant.domain.response.dto.PendingParticipantDto;
import com.gmg.api.Participant.repository.ParticipantRepository;
import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.service.MeetingService;
import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.service.MemberService;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {


    private final ParticipantRepository participantRepository;
    private final MemberService memberService;
    private final MeetingService meetingService;


    @Override
    @Transactional
    public String participantRequest(Long memberId, Long meetingId) {
        // 이미 신청, 승인 이면 예외 발생
        isValidateParticipantRequest(memberId, meetingId);

        Member member = memberService.getReferenceMemberById(memberId); // 프록시
        Meeting meeting = meetingService.getReferenceMeetingById(meetingId); // 프록시

        Participant save = participantRepository.save(Participant.ofRequest(member, meeting));
        return save.getParticipantId() != null ? "신청이 완료되었습니다." : "신청 실패";
    }

    @Override
    public ParticipantListResponse getParticipantList(Long meetingId) {
        List<PendingParticipantDto> pendingParticipantDto = participantRepository.getPendingParticipantListByMeetingId(meetingId);
        List<AcceptedParticipantDto> acceptedParticipantDto = participantRepository.getAcceptedParticipantListByMeetingId(meetingId);

        return ParticipantListResponse.of(pendingParticipantDto, acceptedParticipantDto);
    }

    private void isValidateParticipantRequest(Long memberId, Long meetingId) {
         if(participantRepository.validateParticipantRequest(memberId, meetingId)){
             throw new ResourceAlreadyExistsException("이미 신청되어 있습니다.");
         }
    }
}
