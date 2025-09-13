package com.gmg.api.Participant.service;

import com.gmg.api.Participant.domain.entity.Participant;
import com.gmg.api.Participant.domain.request.ParticipantAcceptedDto;
import com.gmg.api.Participant.domain.response.ParticipantListResponse;
import com.gmg.api.Participant.domain.response.dto.AcceptedParticipantDto;
import com.gmg.api.Participant.domain.response.dto.PendingParticipantDto;
import com.gmg.api.Participant.repository.ParticipantRepository;
import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.service.MeetingService;
import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.service.MemberService;
import com.gmg.api.type.Status;
import com.gmg.global.exception.handelException.MatchMissException;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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

    @Override
    @Transactional
    public String participantAccepted(Long meetingId, Long memberId, ParticipantAcceptedDto participantAcceptedDto) {
        Meeting meeting = meetingService.getMeetingById(meetingId);
        validateMeetingOwner(meeting, memberId); // 방장인지 확인
        validateApprovalOverflow(meetingId, meeting.getPersonCount()); // 승인 인원 초과 검증

        long statusToAccepted = participantRepository.updateParticipantStatusToAccepted(meetingId, participantAcceptedDto.getParticipantId(), Status.APPROVED);
        if (statusToAccepted == 0) {
            throw new MatchMissException("이미 승인되었거나 존재하지 않는 참가자입니다.");
        }

        return "참가자가 승인되었습니다.";
    }

    @Override
    @Transactional
    public String participantReject(Long meetingId, Long memberId, ParticipantAcceptedDto participantAcceptedDto) {
        validateMeetingOwnerBoolean(meetingId, memberId); // boolean 값을 사용 해 방장인지 확인

        // 업데이트
        long statusToAccepted = participantRepository.updateParticipantStatusToAccepted(meetingId, participantAcceptedDto.getParticipantId(), Status.REJECTED);
        if (statusToAccepted == 0) {
            throw new MatchMissException("이미 거절되었거나 존재하지 않는 참가자입니다.");
        }

        return "참가자가 거절되었습니다.";
    }

    private void validateApprovalOverflow(Long meetingId, Integer personCount) {
        Long approvedCount = participantRepository.getPersonCount(meetingId);
        if(personCount.longValue() == approvedCount) {
            throw new MatchMissException("승인 인원이 모두 찼습니다.");
        }
    }

    private void validateMeetingOwnerBoolean(Long meetingId, Long memberId) {
        if (!meetingService.validateMeetingOwner(meetingId, memberId)) {
            throw new MatchMissException("방장만 권한이 있습니다.");
        }
    }

    private void validateMeetingOwner(Meeting meeting, Long memberId) {
        if (!Objects.equals(meeting.getMember().getMemberId(), memberId)) {
            throw new MatchMissException("방장만 권한이 있습니다.");
        }
    }

    private void isValidateParticipantRequest(Long memberId, Long meetingId) {
         if(participantRepository.validateParticipantRequest(memberId, meetingId)){
             throw new ResourceAlreadyExistsException("이미 신청되어 있습니다.");
         }
    }
}
