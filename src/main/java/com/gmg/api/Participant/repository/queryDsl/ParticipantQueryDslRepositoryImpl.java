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

    @Override
    public long updateParticipantStatusToAccepted(Long meetingId, Long participantId, Status status) {
        return queryFactory
                .update(participant)
                .set(participant.status, status)
                .where(
                        meetingIdEq(meetingId),
                        participantIdEq(participantId),
                        participantStatusNe(status)
                )
                .execute();
    }

    // Update 쿼리는 음수를 반환하기 때문에 반환 타입을 long 사용
    // Count() 같은 집계 함수 사용 시 fetchOne()를 쓰면 객체를 반환하기 떄문에 반환값 Long 사용

    @Override
    public Long getPersonCount(Long meetingId) {
        return  queryFactory
                .select(participant.count())
                .from(participant)
                .where(
                        meetingIdEq(meetingId),
                        participant.status.eq(Status.APPROVED)
                )
                .fetchOne();
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
        return participant.member.memberId.eq(memberId);
    }

    private BooleanExpression meetingIdEq(Long meetingId) {
        if (meetingId == null) return null;
        return participant.meeting.meetingId.eq(meetingId);
    }

    private BooleanExpression participantIdEq(Long participantId) {
        if (participantId == null) return null;
        return participant.participantId.eq(participantId);
    }

    private BooleanExpression statusInPendingOrApproved() {
        return participant.status.in(Status.PENDING, Status.APPROVED);
    }

    private BooleanExpression participantStatusNe(Status... statuses) { // Status... 가변 파라미터 : 매개변수를 동적으로 처리
        return participant.status.notIn(statuses);
    }
}
