package com.gmg.api.Participant.repository.queryDsl;

import com.gmg.api.Participant.domain.entity.QParticipant;
import com.gmg.api.type.Status;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.gmg.api.meeting.domain.entity.QMeeting.meeting;
import static com.gmg.api.member.domain.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class ParticipantQueryDslRepositoryImpl implements ParticipantQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean validateParticipantRequest(Long memberId, Long meetingId) {
        QParticipant participant = QParticipant.participant;

        return queryFactory
                .selectFrom(participant)
                .where(
                        participant.member.memberId.eq(memberId)
                                .and(participant.meeting.meetingId.eq(meetingId))
                                .and(participant.status.in(Status.PENDING, Status.APPROVED))
                )
                .fetchFirst() != null; // 존재하면 true 반환
    }

}
