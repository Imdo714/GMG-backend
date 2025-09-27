package com.gmg.api.Participant.repository.queryDsl;

import com.gmg.api.Participant.domain.response.dto.AcceptedParticipantDto;
import com.gmg.api.Participant.domain.response.dto.PendingParticipantDto;
import com.gmg.api.Participant.domain.response.dto.HistoryDto;
import com.gmg.api.type.Status;

import java.util.List;
import java.util.Map;

public interface ParticipantQueryDslRepository {

    boolean validateParticipantRequest(Long memberId, Long meetingId);
    List<PendingParticipantDto> getPendingParticipantListByMeetingId(Long meetingId);
    List<AcceptedParticipantDto> getAcceptedParticipantListByMeetingId(Long meetingId);
    Long getAcceptedPersonCountByMeetingId(Long meetingId);
    long updateParticipantStatus(Long meetingId, Long participantId, Status status);
    Map<Long, Long> getAcceptedCountsByMeetingIds(List<Long> collect);
    List<HistoryDto> historyParticipantReview(Long meetingId);
    boolean areParticipantsInSameMeeting(Long meetingId, Long targetMemberId, Long writerMemberId);
}
