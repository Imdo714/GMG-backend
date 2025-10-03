package com.gmg.api.Participant.repository.queryDsl;

import com.gmg.api.Participant.domain.response.dto.HistoryDto;
import com.gmg.api.Participant.domain.response.dto.MeetingApprovalCheckDto;
import com.gmg.api.Participant.domain.response.dto.ParticipantDto;
import com.gmg.api.Participant.domain.response.dto.ParticipantLogDto;

import java.util.List;

public interface ParticipantQueryDslRepository {
    List<HistoryDto> historyParticipantReview(Long meetingId);
    boolean areParticipantsInSameMeeting(Long meetingId, Long targetMemberId, Long writerMemberId);
    List<ParticipantLogDto> getParticipantLogList(Long memberId);
    long approveParticipant(Long meetingId, Long participantId, Long ownerMemberId);
    long rejectParticipant(Long meetingId, Long participantId, Long ownerMemberId);
    long deleteParticipant(Long meetingId, Long participantId, Long memberId);
    MeetingApprovalCheckDto getApprovalCheck(Long meetingId);
    List<ParticipantDto> testGetPendingParticipantListByMeetingId(Long meetingId);
}
