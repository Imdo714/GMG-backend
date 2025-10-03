package com.gmg.api.Participant.service.query;

import com.gmg.api.Participant.domain.response.ParticipantListResponse;
import com.gmg.api.Participant.domain.response.dto.AcceptedParticipantDto;
import com.gmg.api.Participant.domain.response.dto.ParticipantDto;
import com.gmg.api.Participant.domain.response.dto.PendingParticipantDto;
import com.gmg.api.Participant.repository.ParticipantRepository;
import com.gmg.api.type.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantQueryServiceImpl implements ParticipantQueryService {

    private final ParticipantRepository participantRepository;

    @Override
    public ParticipantListResponse getParticipantList(Long meetingId) {
        List<ParticipantDto> participantDto = participantRepository.testGetPendingParticipantListByMeetingId(meetingId);

        List<PendingParticipantDto> pending = participantDto.stream()
                .filter(p -> p.getStatus() == Status.PENDING)
                .map(PendingParticipantDto::toPendingDto)
                .toList();

        List<AcceptedParticipantDto> accepted = participantDto.stream()
                .filter(a -> a.getStatus() == Status.APPROVED)
                .map(AcceptedParticipantDto::toAcceptedDto)
                .toList();

        return ParticipantListResponse.of(pending, accepted);
    }

    /**
     * Stream 방식은 participantDto 컬렉션을 2번 순회하여 상태별로 필터링하기 때문에
     * 데이터가 많아질 경우(수만 건 이상)에는 비효율적일 수 있다.
     *
     * forEach 방식은 컬렉션을 한 번만 순회하면서 상태별로 리스트에 추가하기 때문에
     * 성능 면에서는 더 효율적이다.
     *
     * 하지만, 본 프로젝트의 모임 신청자 리스트는 데이터가 많아야 수십 건 수준이므로
     * 성능 차이는 거의 없고, 가독성을 고려하여 Stream 방식을 선택하였다.
     *
     *     @Override
     *     public ParticipantListResponse getParticipantList(Long meetingId) {
     *         List<PendingParticipantDto> pending = new ArrayList<>();
     *         List<AcceptedParticipantDto> accepted = new ArrayList<>();
     *
     *         List<ParticipantDto> participantDto = participantRepository.testGetPendingParticipantListByMeetingId(meetingId);
     *
     *         participantDto.forEach(p -> {
     *             if (p.getStatus() == Status.PENDING) {
     *                 pending.add(toPendingDto(p));
     *             } else if (p.getStatus() == Status.APPROVED) {
     *                 accepted.add(toAcceptedDto(p));
     *             }
     *         });
     *
     *         return ParticipantListResponse.of(pending, accepted);
     *     }
     * */

}
