package com.gmg.api.Participant.service.query;

import com.gmg.api.Participant.domain.response.ParticipantListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantQueryServiceImpl implements ParticipantQueryService {

    @Override
    public ParticipantListResponse getParticipantList(Long meetingId) {
        return null;
    }
}
