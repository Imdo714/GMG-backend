package com.gmg.api.Participant.repository.queryDsl;

import com.gmg.api.Participant.domain.entity.QParticipant;
import com.gmg.api.Participant.domain.response.dto.HistoryDto;
import com.gmg.api.Participant.domain.response.dto.MeetingApprovalCheckDto;
import com.gmg.api.Participant.domain.response.dto.ParticipantDto;
import com.gmg.api.Participant.domain.response.dto.ParticipantLogDto;
import com.gmg.api.meeting.domain.entity.QMeeting;
import com.gmg.api.member.domain.entity.QMember;
import com.gmg.api.review.domain.entity.QReview;
import com.gmg.api.review.domain.response.dto.ReviewCheckInfo;
import com.gmg.api.type.Status;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ParticipantQueryDslRepositoryImpl implements ParticipantQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QParticipant participant = QParticipant.participant;
    private final QMember member = QMember.member;
    private final QMeeting meeting =QMeeting.meeting;
    private final QReview review = QReview.review;

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

    // Update 쿼리는 음수를 반환하기 때문에 반환 타입을 long 사용 (절대 Null일수 없기 때문에 )
    // Count() 같은 집계 함수 사용 시 fetchOne()를 쓰면 객체를 반환하기 떄문에 반환값 Long 사용

    @Override
    public long approveParticipant(Long meetingId, Long participantId, Long ownerMemberId) {
        return queryFactory
                .update(participant)
                .set(participant.status, Status.APPROVED)
                .where(
                        participant.participantId.eq(participantId),               // 대상 참가자
                        participant.meeting.meetingId.eq(meetingId),              // 해당 모임
                        participant.status.eq(Status.PENDING),                   // 승인 대기 상태
                        participant.meeting.member.memberId.eq(ownerMemberId),   // 방장 체크
                        participant.member.memberId.ne(ownerMemberId)           // 자기 자신은 제외
                )
                .execute();
    }

    @Override
    public long rejectParticipant(Long meetingId, Long participantId, Long ownerMemberId) {
        return queryFactory
                .update(participant)
                .set(participant.status, Status.REJECTED)
                .where(
                        participant.participantId.eq(participantId),
                        participant.meeting.meetingId.eq(meetingId),
                        participant.status.in(Status.PENDING, Status.APPROVED), // 둘 다 거절 가능
                        participant.meeting.member.memberId.eq(ownerMemberId),
                        participant.member.memberId.ne(ownerMemberId)
                )
                .execute();
    }

    @Override
    public long deleteParticipant(Long meetingId, Long participantId, Long memberId) {
        return queryFactory
                .delete(participant)
                .where(
                        participant.participantId.eq(participantId),    //  삭제할 ID
                        participant.meeting.meetingId.eq(meetingId),    // 삭제할 모임 ID
                        participant.member.memberId.eq(memberId),       // 자기 자신만 삭제
                        participant.member.memberId.ne(participant.meeting.member.memberId) // 방장은 삭제 불가
                )
                .execute();
    }

    @Override
    public MeetingApprovalCheckDto getApprovalCheck(Long meetingId) {
        return queryFactory
                .select(Projections.constructor(MeetingApprovalCheckDto.class,
                        meeting.personCount,
                        participant.count()
                ))
                .from(meeting)
                .leftJoin(participant).on(
                        participant.meeting.meetingId.eq(meeting.meetingId),
                        participant.status.eq(Status.APPROVED)
                )
                .where(meeting.meetingId.eq(meetingId))
                .groupBy(meeting.personCount)
                .fetchOne();
    }

    @Override
    public List<ParticipantDto> testGetPendingParticipantListByMeetingId(Long meetingId) {
        return queryFactory
                .select(Projections.constructor(ParticipantDto.class,
                        participant.participantId,
                        member.memberId,
                        member.profile,
                        member.name,
                        participant.status
                ))
                .from(participant)
                .join(participant.member, member)
                .where(
                        meetingIdEq(meetingId)
                )
                .fetch();
    }

    @Override
    public Optional<ReviewCheckInfo> getReviewCheckInfo(Long meetingId, Long targetMemberId, Long writerMemberId) {
        return Optional.ofNullable(queryFactory
                .select(Projections.constructor(ReviewCheckInfo.class,
                        meeting.date,
                        meeting.time,
                        participant.member.countDistinct()
                ))
                .from(participant)
                .join(participant.meeting, meeting)
                .where(
                        meeting.meetingId.eq(meetingId),
                        participant.member.memberId.in(writerMemberId, targetMemberId), // 작성자, 받는자 모임에 있는지
                        participant.status.eq(Status.APPROVED)
                )
                .fetchOne());
    }

    private BooleanExpression pastMeetingCondition(LocalDate nowDate, LocalTime nowTime) {
        if (nowDate == null || nowTime == null) {
            return null;
        }
        return meeting.date.before(nowDate)
                .or(meeting.date.eq(nowDate).and(meeting.time.before(nowTime)));
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

    private BooleanExpression statusEqApproved() {
        return participant.status.eq(Status.APPROVED);
    }

}
