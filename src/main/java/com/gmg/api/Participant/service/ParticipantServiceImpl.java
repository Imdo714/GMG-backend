package com.gmg.api.Participant.service;

import com.gmg.api.Participant.domain.entity.Participant;
import com.gmg.api.Participant.domain.request.ParticipantIdDto;
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
        isValidateParticipantRequest(memberId, meetingId);  // 이미 신청 거절, 승인 이면 예외 발생

        Member member = getReferenceMemberById(memberId); // 프록시
        Meeting meeting = getReferenceMeetingById(meetingId); // 프록시

        Participant save = participantRepository.save(Participant.ofRequest(member, meeting));
        return save.getParticipantId() != null ? "신청이 완료되었습니다." : "신청 실패";
    }

    @Override
    public ParticipantListResponse getParticipantList(Long meetingId) {
        return ParticipantListResponse.of(getPendingParticipantListByMeetingId(meetingId), getAcceptedParticipantListByMeetingId(meetingId));
    }

    @Override
    @Transactional
    public String participantAccepted(Long meetingId, Long memberId, ParticipantIdDto participantIdDto) {
        Meeting meeting = getMeetingById(meetingId);
        validateMeetingOwner(meeting, memberId); // 방장인지 확인
        validateApprovalOverflow(meetingId, meeting.getPersonCount()); // 승인 인원 초과 검증

        long statusToAccepted = updateParticipantStatus(meetingId, participantIdDto, Status.APPROVED);
        validateParticipantUpdateResult(statusToAccepted, "이미 승인되었거나 존재하지 않는 참가자입니다.");

        return "참가자가 승인되었습니다.";
    }

    @Override
    @Transactional
    public String participantReject(Long meetingId, Long memberId, ParticipantIdDto participantIdDto) {
        validateMeetingOwnerBoolean(meetingId, memberId); // boolean 값을 사용 해 방장인지 확인

        long statusToRejected = updateParticipantStatus(meetingId, participantIdDto, Status.REJECTED);
        validateParticipantUpdateResult(statusToRejected, "이미 거절되었거나 존재하지 않는 참가자입니다.");

        return "참가자가 거절되었습니다.";
    }

    @Override
    @Transactional
    public String participantCancel(Long meetingId, Long memberId, ParticipantIdDto participantIdDto) {
        // 모임 승인, 대기 상태여도 삭제할 수 있어야 함
        validateParticipantOwnership(memberId, participantIdDto);
        participantRepository.deleteById(participantIdDto.getParticipantId());
        return "모임을 취소하였습니다.";
    }

    // 참가 신청자가 맞는지 검증
    private void validateParticipantOwnership(Long memberId, ParticipantIdDto participantIdDto) {
        Participant participant = getParticipantById(participantIdDto.getParticipantId());

        // Null이 들어올수 없으니 직접 equals() 호출
        if(!memberId.equals(participant.getMember().getMemberId())){
            throw new MatchMissException("신청자만 모임 취소를 할수 있습니다.");
        }
    }

    // 참가 신청 상태 값 변경 실패 예외 메서드
    private void validateParticipantUpdateResult(long updated, String errorMessage) {
        if (updated == 0) throw new MatchMissException(errorMessage);
    }

    // 참가 신청 상태 값 변경 메서드
    private long updateParticipantStatus(Long meetingId, ParticipantIdDto dto, Status status) {
        return participantRepository.updateParticipantStatus(meetingId, dto.getParticipantId(), status);
    }

    // Meeting 객체 반환 메서드
    private Meeting getMeetingById(Long meetingId) {
        return meetingService.getMeetingById(meetingId);
    }

    // Meeting 프록시 반환 메서드
    private Meeting getReferenceMeetingById(Long meetingId) {
        return meetingService.getReferenceMeetingById(meetingId);
    }

    // Participant 객체 반환 메서드
    private Participant getParticipantById(Long participantId) {
        return participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceAlreadyExistsException("존재하지 않는 신청입니다."));
    }

    // Member 프록시 반환 메서드
    private Member getReferenceMemberById(Long memberId) {
        return memberService.getReferenceMemberById(memberId);
    }

    // 신청 대기 리스트 반환 메서드
    private List<PendingParticipantDto> getPendingParticipantListByMeetingId(Long meetingId) {
        return participantRepository.getPendingParticipantListByMeetingId(meetingId);
    }

    // 신청 승인 리스트 반환 메서드
    private List<AcceptedParticipantDto> getAcceptedParticipantListByMeetingId(Long meetingId) {
        return participantRepository.getAcceptedParticipantListByMeetingId(meetingId);
    }

    // 승인 인원 갯수 조회 메서드
    private Long getAcceptedPersonCountByMeetingId(Long meetingId) {
        return participantRepository.getAcceptedPersonCountByMeetingId(meetingId);
    }

    // 승인 인원 다 찼을 시 예외 메서드
    private void validateApprovalOverflow(Long meetingId, Integer personCount) {
        Long approvedCount = getAcceptedPersonCountByMeetingId(meetingId);
        if(personCount.longValue() == approvedCount) {
            throw new MatchMissException("승인 인원이 모두 찼습니다.");
        }
    }

    // meeting 객체를 사용안하고 meetingId 만 사용해도 될때 방장 여부 판단 메서드
    private void validateMeetingOwnerBoolean(Long meetingId, Long memberId) {
        if (!meetingService.validateMeetingOwner(meetingId, memberId)) {
            throw new MatchMissException("방장만 권한이 있습니다.");
        }
    }

    // meeting 을 사용해야 할때 방장 여부 판단 메서드
    private void validateMeetingOwner(Meeting meeting, Long memberId) {
        // Null 이 들어올 수 있으면 Objects.equals() 사용
        if (!Objects.equals(meeting.getMember().getMemberId(), memberId)) {
            throw new MatchMissException("방장만 권한이 있습니다.");
        }
    }

    // 이미 신청 또는 거절 상태 시 예외 메서드
    private void isValidateParticipantRequest(Long memberId, Long meetingId) {
         if(participantRepository.validateParticipantRequest(memberId, meetingId)){
             throw new ResourceAlreadyExistsException("이미 신청되었거나 거절되었습니다.");
         }
    }
}
