package com.gmg.api.Participant.repository.queryDsl;

import com.gmg.api.Participant.domain.entity.QParticipant;
import com.gmg.api.Participant.domain.response.dto.AcceptedParticipantDto;
import com.gmg.api.Participant.domain.response.dto.HistoryDto;
import com.gmg.api.Participant.domain.response.dto.ParticipantLogDto;
import com.gmg.api.Participant.domain.response.dto.PendingParticipantDto;
import com.gmg.api.meeting.domain.entity.QMeeting;
import com.gmg.api.member.domain.entity.QMember;
import com.gmg.api.review.domain.entity.QReview;
import com.gmg.api.type.Status;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ParticipantQueryDslRepositoryImpl implements ParticipantQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QParticipant participant = QParticipant.participant;
    private final QMember member = QMember.member;
    private final QMeeting meeting =QMeeting.meeting;
    private final QReview review = QReview.review;

    @Override
    public List<PendingParticipantDto> getPendingParticipantListByMeetingId(Long meetingId) {
        return getParticipantListByMeetingId(meetingId, Status.PENDING, PendingParticipantDto.class);
    }

    @Override
    public List<AcceptedParticipantDto> getAcceptedParticipantListByMeetingId(Long meetingId) {
        return getParticipantListByMeetingId(meetingId, Status.APPROVED, AcceptedParticipantDto.class);
    }

    @Override
    public long updateParticipantStatus(Long meetingId, Long participantId, Status status) {
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
    public Long getAcceptedPersonCountByMeetingId(Long meetingId) {
        return  queryFactory
                .select(participant.count())
                .from(participant)
                .where(
                        meetingIdEq(meetingId),
                        statusEqApproved()
                )
                .fetchOne();
    }

    @Override
    public List<HistoryDto> historyParticipantReview(Long meetingId) {
        return queryFactory
                .select(Projections.constructor(HistoryDto.class,
                        member.memberId,
                        member.profile,
                        member.name,
                        review.comment
                ))
                .from(participant)
                .join(participant.member, member)
                .join(participant.meeting, meeting)
                .leftJoin(review).on(
                        review.meeting.eq(meeting)      // 같은 모임에서 작성된 리뷰
                                .and(review.reviewee.eq(member)) // 리뷰 대상자가 참가자(member)
                )
                .where(
                        meetingIdEq(meetingId),
                        statusEqApproved()
                )
                .fetch();
    }

    @Override
    public boolean areParticipantsInSameMeeting(Long meetingId, Long targetMemberId, Long writerMemberId) {
        if (targetMemberId.equals(writerMemberId)) {
            return false;
        }

        Long count = queryFactory
                .select(participant.member.memberId.countDistinct())
                .from(participant)
                .where(
                        meetingIdEq(meetingId),
                        participant.member.memberId.in(targetMemberId, writerMemberId),
                        statusEqApproved()
                )
                .fetchOne();

        // 두 명 모두 존재해야 true
        return count != null && count == 2;
    }

    @Override
    public List<ParticipantLogDto> getParticipantLogList(Long memberId) {
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        return queryFactory
                .select(Projections.constructor(ParticipantLogDto.class,
                        meeting.meetingId,
                        meeting.title,
                        meeting.category
                ))
                .from(participant)
                .join(participant.meeting, meeting)
                .where(
                        memberIdEq(memberId),
                        statusEqApproved(),
                        pastMeetingCondition(nowDate, nowTime)
                )
                .fetch();
    }

    private BooleanExpression pastMeetingCondition(LocalDate nowDate, LocalTime nowTime) {
        if (nowDate == null || nowTime == null) {
            return null;
        }
        return meeting.date.before(nowDate)
                .or(meeting.date.eq(nowDate).and(meeting.time.before(nowTime)));
    }

    private <T> List<T> getParticipantListByMeetingId(Long meetingId, Status status, Class<T> dtoClass) {
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

    private BooleanExpression statusEqApproved() {
        return participant.status.eq(Status.APPROVED);
    }

    private BooleanExpression statusInPendingOrApproved() {
        return participant.status.in(Status.PENDING, Status.APPROVED, Status.REJECTED);
    }

    private BooleanExpression participantStatusNe(Status... statuses) { // Status... 가변 파라미터 : 매개변수를 동적으로 처리
        return participant.status.notIn(statuses);
    }
}
