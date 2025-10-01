package com.gmg.api.Participant.service;

import com.gmg.api.Participant.domain.request.ParticipantIdDto;
import com.gmg.api.Participant.domain.response.ParticipantListResponse;
import com.gmg.api.Participant.domain.response.ParticipantLogListResponse;
import com.gmg.api.Participant.domain.response.dto.AcceptedParticipantDto;
import com.gmg.api.Participant.domain.response.dto.HistoryDto;
import com.gmg.api.Participant.domain.response.dto.ParticipantLogDto;
import com.gmg.api.Participant.domain.response.dto.PendingParticipantDto;
import com.gmg.api.Participant.repository.ParticipantRepository;
import com.gmg.api.meeting.service.MeetingService;
import com.gmg.api.member.service.MemberService;
import com.gmg.api.type.Status;
import com.gmg.global.exception.handelException.MatchMissException;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final MeetingService meetingService;

    @Override
    public ParticipantListResponse getParticipantList(Long meetingId) {
        return ParticipantListResponse.of(getPendingParticipantListByMeetingId(meetingId), getAcceptedParticipantListByMeetingId(meetingId));
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
        validateParticipantOwnership(meetingId, memberId); // 방장이면 예외
        validateParticipantIsRequester(memberId, participantIdDto.getParticipantId()); // 다른 신청자면 예외

        participantRepository.deleteById(participantIdDto.getParticipantId());
        return "모임을 취소하였습니다.";
    }

    @Override
    public ParticipantLogListResponse getParticipantLogList(Long memberId) {
        // 1. 참여했던 모임 ID, 제목
        List<ParticipantLogDto> participantLogList = participantRepository.getParticipantLogList(memberId);

        Map<String, Long> stats = participantLogList.stream()
                .collect(Collectors.groupingBy(
                        meeting -> meeting.getCategory().name(),
                        Collectors.counting() // 각 그룹의 개수를 센다
                ));

        return ParticipantLogListResponse.builder()
                .logList(participantLogList)
                .stats(stats)
                .participantCount(participantLogList.size())
                .build();
    }

    @Override
    public List<HistoryDto> historyParticipantReview(Long meetingId) {
        return participantRepository.historyParticipantReview(meetingId);
    }

    // 방장이면 모임을 취소 예외 메서드
    private void validateParticipantOwnership(Long meetingId, Long memberId) {
        // Meeting 모임 만든 사람의 Id를 찾는 메서드
        Long ownerId = meetingService.getMakeMeetingOwner(meetingId);
        
        if (Objects.equals(ownerId, memberId)) {
            throw new MatchMissException("방장은 모임을 취소 할 수 없습니다.");
        }
    }

    // 참가 신청자가 맞는지 검증
    private void validateParticipantIsRequester(Long memberId, Long participantId) {
        Long requestParticipantMemberId = getRequestParticipantMemberId(participantId);

        // Null이 들어올수 없으니 직접 equals() 호출
        if(!memberId.equals(requestParticipantMemberId)){
            throw new MatchMissException("신청자만 모임 취소를 할수 있습니다.");
        }
    }

    // 신청 한 사람의 MemberId 반환
    private Long getRequestParticipantMemberId(Long participantId) {
         return participantRepository.getRequestParticipantMemberId(participantId)
                .orElseThrow(() -> new ResourceAlreadyExistsException("존재하지 않는 신청입니다."));
    }

    // 참가 신청 상태 값 변경 실패 예외 메서드
    private void validateParticipantUpdateResult(long updated, String errorMessage) {
        if (updated == 0) throw new MatchMissException(errorMessage);
    }

    // 참가 신청 상태 값 변경 메서드
    private long updateParticipantStatus(Long meetingId, ParticipantIdDto dto, Status status) {
        return participantRepository.updateParticipantStatus(meetingId, dto.getParticipantId(), status);
    }

    // 신청 대기 리스트 반환 메서드
    private List<PendingParticipantDto> getPendingParticipantListByMeetingId(Long meetingId) {
        return participantRepository.getPendingParticipantListByMeetingId(meetingId);
    }

    // 신청 승인 리스트 반환 메서드
    private List<AcceptedParticipantDto> getAcceptedParticipantListByMeetingId(Long meetingId) {
        return participantRepository.getAcceptedParticipantListByMeetingId(meetingId);
    }

    // meeting 객체를 사용안하고 meetingId 만 사용해도 될때 방장 여부 판단 메서드
    private void validateMeetingOwnerBoolean(Long meetingId, Long memberId) {
        if (!meetingService.validateMeetingOwner(meetingId, memberId)) {
            throw new MatchMissException("방장만 권한이 있습니다.");
        }
    }

}
