package com.gmg.api.Participant.repository.queryDsl;

import com.gmg.api.Participant.domain.response.dto.*;
import com.gmg.api.type.Status;

import java.util.List;

public interface ParticipantQueryDslRepository {
    List<PendingParticipantDto> getPendingParticipantListByMeetingId(Long meetingId);
    List<AcceptedParticipantDto> getAcceptedParticipantListByMeetingId(Long meetingId);
    long updateParticipantStatus(Long meetingId, Long participantId, Status status);
    List<HistoryDto> historyParticipantReview(Long meetingId);
    boolean areParticipantsInSameMeeting(Long meetingId, Long targetMemberId, Long writerMemberId);
    List<ParticipantLogDto> getParticipantLogList(Long memberId);
    long approveParticipant(Long meetingId, Long participantId, Long ownerMemberId);
    long rejectParticipant(Long meetingId, Long participantId, Long ownerMemberId);
    long deleteParticipant(Long meetingId, Long participantId, Long memberId);
    MeetingApprovalCheckDto getApprovalCheck(Long meetingId);
}
