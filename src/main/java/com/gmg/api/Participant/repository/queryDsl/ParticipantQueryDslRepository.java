package com.gmg.api.Participant.repository.queryDsl;

import com.gmg.api.Participant.domain.response.dto.AcceptedParticipantDto;
import com.gmg.api.Participant.domain.response.dto.HistoryDto;
import com.gmg.api.Participant.domain.response.dto.ParticipantLogDto;
import com.gmg.api.Participant.domain.response.dto.PendingParticipantDto;
import com.gmg.api.type.Status;

import java.util.List;

public interface ParticipantQueryDslRepository {
    List<PendingParticipantDto> getPendingParticipantListByMeetingId(Long meetingId);
    List<AcceptedParticipantDto> getAcceptedParticipantListByMeetingId(Long meetingId);
    Long getAcceptedPersonCountByMeetingId(Long meetingId);
    long updateParticipantStatus(Long meetingId, Long participantId, Status status);
    List<HistoryDto> historyParticipantReview(Long meetingId);
    boolean areParticipantsInSameMeeting(Long meetingId, Long targetMemberId, Long writerMemberId);
    List<ParticipantLogDto> getParticipantLogList(Long memberId);
}
