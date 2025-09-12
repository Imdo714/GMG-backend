package com.gmg.api.Participant.repository.queryDsl;

import com.gmg.api.Participant.domain.entity.QParticipant;
import com.gmg.api.Participant.domain.response.dto.AcceptedParticipantDto;
import com.gmg.api.Participant.domain.response.dto.PendingParticipantDto;
import com.gmg.api.member.domain.entity.QMember;
import com.gmg.api.type.Status;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ParticipantQueryDslRepositoryImpl implements ParticipantQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QParticipant participant = QParticipant.participant;

    @Override
    public boolean validateParticipantRequest(Long memberId, Long meetingId) {
        return queryFactory
                .selectFrom(participant)
                .where(
                        memberIdEq(memberId),
                        meetingIdEq(meetingId),
                        statusInPendingOrApproved()
                )
                .fetchFirst() != null; // 존재하면 true 반환
    }

    @Override
    public List<PendingParticipantDto> getPendingParticipantListByMeetingId(Long meetingId) {
        return getParticipantListByMeetingId(meetingId, Status.PENDING, PendingParticipantDto.class);
    }

    @Override
    public List<AcceptedParticipantDto> getAcceptedParticipantListByMeetingId(Long meetingId) {
        return getParticipantListByMeetingId(meetingId, Status.APPROVED, AcceptedParticipantDto.class);
    }

    private <T> List<T> getParticipantListByMeetingId(Long meetingId, Status status, Class<T> dtoClass) {
        QMember member = QMember.member;

        return queryFactory
                .select(Projections.constructor(dtoClass,
                        participant.participantId,
                        member.memberId,
                        member.profile,
                        member.name
                ))
                .from(participant)
                .join(participant.member, member)
                .where(
                        meetingIdEq(meetingId),
                        participant.status.eq(status)
                )
                .fetch();
    }

    // BooleanExpression 는 QueryDSL 에서 WHERE 절을 표현하는 객체
    private BooleanExpression memberIdEq(Long memberId) {
        if (memberId == null) return null; // null이면 조건 무시
        return QParticipant.participant.member.memberId.eq(memberId);
    }

    private BooleanExpression meetingIdEq(Long meetingId) {
        if (meetingId == null) return null;
        return QParticipant.participant.meeting.meetingId.eq(meetingId);
    }

    private BooleanExpression statusInPendingOrApproved() {
        return QParticipant.participant.status.in(Status.PENDING, Status.APPROVED);
    }
}
