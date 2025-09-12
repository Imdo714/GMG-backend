package com.gmg.api.Participant.repository.queryDsl;

public interface ParticipantQueryDslRepository {

    boolean validateParticipantRequest(Long memberId, Long meetingId);

}
