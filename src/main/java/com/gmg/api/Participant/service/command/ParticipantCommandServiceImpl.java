package com.gmg.api.Participant.service.command;

import com.gmg.api.Participant.domain.entity.Participant;
import com.gmg.api.Participant.domain.request.ParticipantIdDto;
import com.gmg.api.Participant.domain.response.dto.ValidateRequest;
import com.gmg.api.Participant.repository.ParticipantRepository;
import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.repository.MeetingRepository;
import com.gmg.api.meeting.service.MeetingService;
import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.service.MemberService;
import com.gmg.api.type.Status;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
        ValidateRequest result = meetingRepository.validateParticipantRequest(memberId, meetingId);
        validateException(result);

        Meeting meeting = getReferenceMeetingById(meetingId); // 프록시
        Member member = getReferenceMemberById(memberId); // 프록시

        Participant save = participantRepository.save(Participant.ofRequest(member, meeting));
        return save.getParticipantId() != null ? "신청이 완료되었습니다." : "신청 실패";
    }

    @Override
    public String participantAccepted(Long meetingId, Long memberId, ParticipantIdDto participantIdDto) {
        return null;
    }

    @Override
    public String participantReject(Long meetingId, Long memberId, ParticipantIdDto participantIdDto) {
        return null;
    }

    @Override
    public String participantCancel(Long meetingId, Long memberId, ParticipantIdDto participantIdDto) {
        return null;
    }

    private void validateException(ValidateRequest result) {
        if (result.getParticipantId() != null && result.getStatus() == Status.REJECTED) {
            throw new ResourceAlreadyExistsException("이미 신청되었거나 거절되었습니다.");
        }

        if (LocalDateTime.of(result.getDate(), result.getTime()).isBefore(LocalDateTime.now())) {
            throw new ResourceAlreadyExistsException("이미 지난 모임입니다.");
        }
    }

    // Meeting 프록시 반환 메서드
    private Meeting getReferenceMeetingById(Long meetingId) {
        return meetingService.getReferenceMeetingById(meetingId);
    }

    // Member 프록시 반환 메서드
    private Member getReferenceMemberById(Long memberId) {
        return memberService.getReferenceMemberById(memberId);
    }
}
