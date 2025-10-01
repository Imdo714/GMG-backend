package com.gmg.api.Participant.service.command;

import com.gmg.api.Participant.domain.entity.Participant;
import com.gmg.api.Participant.domain.request.ParticipantIdDto;
import com.gmg.api.Participant.domain.response.dto.MeetingApprovalCheckDto;
import com.gmg.api.meeting.domain.response.dto.MeetingValidationContext;
import com.gmg.api.Participant.repository.ParticipantRepository;
import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.repository.MeetingRepository;
import com.gmg.api.meeting.service.MeetingService;
import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.service.MemberService;
import com.gmg.global.exception.handelException.MatchMissException;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantCommandServiceImpl implements ParticipantCommandService {

    private final ParticipantRepository participantRepository;
    private final MeetingRepository meetingRepository;
    private final MemberService memberService;
    private final MeetingService meetingService;

    @Override
    @Transactional
    public String participantRequest(Long memberId, Long meetingId) {
        MeetingValidationContext context = meetingRepository.validateParticipantRequest(memberId, meetingId);
        validateException(context);

        Meeting meeting = getReferenceMeetingById(meetingId); // 프록시
        Member member = getReferenceMemberById(memberId); // 프록시

        Participant save = participantRepository.save(Participant.ofRequest(member, meeting));
        return save.getParticipantId() != null ? "신청이 완료되었습니다." : "신청 실패";
    }

    @Override
    @Transactional
    public String participantAccepted(Long meetingId, Long memberId, ParticipantIdDto participantIdDto) {
        MeetingApprovalCheckDto approvalCheck = participantRepository.getApprovalCheck(meetingId);
        validateCapacityAvailable(approvalCheck);

        Long updated = participantRepository.getAcceptedPersonCountByMeetingId(meetingId, participantIdDto.getParticipantId(), memberId);
        validateParticipantUpdated(updated);

        return "참가자가 승인되었습니다.";
    }

    @Override
    public String participantReject(Long meetingId, Long memberId, ParticipantIdDto participantIdDto) {
        return null;
    }

    @Override
    public String participantCancel(Long meetingId, Long memberId, ParticipantIdDto participantIdDto) {
        return null;
    }

    // Meeting 프록시 반환 메서드
    private Meeting getReferenceMeetingById(Long meetingId) {
        return meetingService.getReferenceMeetingById(meetingId);
    }

    // Member 프록시 반환 메서드
    private Member getReferenceMemberById(Long memberId) {
        return memberService.getReferenceMemberById(memberId);
    }

    // 모임 신청 예외 여부 검증
    private void validateException(MeetingValidationContext context) {
        if (context.isAlreadyRejected()) {
            throw new ResourceAlreadyExistsException("이미 신청되었거나 거절되었습니다.");
        }

        if (context.isExpired()) {
            throw new ResourceAlreadyExistsException("이미 만료 된 모임입니다.");
        }
    }

    // 승인 인원이 정원 초과되었는지 검증
    private void validateCapacityAvailable(MeetingApprovalCheckDto approvalCheck) {
        if (approvalCheck.isFull()) {
            throw new MatchMissException("한정된 인원이 마감되었습니다.");
        }
    }

    // 참가자 상태 업데이트 성공 여부 검증
    private void validateParticipantUpdated(Long updated) {
        if (updated == 0) {
            throw new MatchMissException("이미 승인되었거나 존재하지 않는 참가자입니다.");
        }
    }
}
