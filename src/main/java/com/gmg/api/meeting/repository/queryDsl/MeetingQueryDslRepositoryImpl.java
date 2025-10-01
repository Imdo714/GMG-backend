package com.gmg.api.meeting.repository.queryDsl;

import com.gmg.api.Participant.domain.entity.QParticipant;
import com.gmg.api.meeting.domain.response.dto.MeetingValidationContext;
import com.gmg.api.meeting.domain.entity.QMeeting;
import com.gmg.api.meeting.domain.response.MeetingDetailStaticResponse;
import com.gmg.api.meeting.domain.response.MeetingHistoryResponse;
import com.gmg.api.meeting.domain.response.MeetingListResponse;
import com.gmg.api.type.Category;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.gmg.api.type.Status.APPROVED;

@RequiredArgsConstructor
@Repository
public class MeetingQueryDslRepositoryImpl implements MeetingQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QMeeting meeting = QMeeting.meeting;
    private final QParticipant participant = QParticipant.participant;

    @Override
    public List<MeetingListResponse.MeetingListDto> getMeetingList(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category) {
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        return queryFactory
                .select(Projections.constructor(MeetingListResponse.MeetingListDto.class,
                        meeting.meetingId,
                        meeting.title,
                        meeting.date,
                        meeting.time,
                        meeting.category,
                        meeting.personCount,
                        meeting.seeCount,
                        participant.countDistinct(),
                        Expressions.cases()
                                .when(meeting.date.before(nowDate)
                                        .or(meeting.date.eq(nowDate)
                                                .and(meeting.time.before(nowTime))
                                        )
                                )
                                .then(true)
                                .otherwise(false)
                ))
                .from(meeting)
                .leftJoin(participant).on(
                        participant.meeting.meetingId.eq(meeting.meetingId),
                        participant.status.eq(APPROVED)
                )
                .where(
                        dateTimeCondition(lastMeetingDate, lastMeetingTime, lastMeetingId),
                        categoryEq(category)
                )
                .groupBy(meeting.meetingId)
                .orderBy(meeting.date.desc(), meeting.time.desc(), meeting.meetingId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public boolean existsByMeetingIdAndMember_MemberId(Long meetingId, Long memberId) {
        return queryFactory
                .selectOne()
                .from(meeting)
                .where(
                        meetingIdEq(meetingId),
                        meetingMemberIdEq(memberId)
                )
                .fetchFirst() != null;
    }

    @Override
    public Optional<MeetingDetailStaticResponse.MeetingDetail> meetingDetailStatic(Long meetingId) {
        return Optional.ofNullable(queryFactory
                .select(Projections.constructor(MeetingDetailStaticResponse.MeetingDetail.class,
                        meeting.meetingId,
                        meeting.member.memberId,
                        meeting.title,
                        meeting.content,
                        meeting.category,
                        meeting.address,
                        meeting.addressDetail,
                        meeting.personCount,
                        meeting.date,
                        meeting.time
                ))
                .from(meeting)
                .where(
                        meetingIdEq(meetingId)
                )
                .fetchOne());
    }

    @Override
    public void updateSeeCount(Map<Long, Integer> meetingViewCounts) {
        if (meetingViewCounts == null || meetingViewCounts.isEmpty()) {
            return;
        }

        CaseBuilder.Cases<Integer, NumberExpression<Integer>> cases = null;

        // Map의 데이터를 기반으로 CASE WHEN ... THEN ... 구문 생성
        for (Map.Entry<Long, Integer> entry : meetingViewCounts.entrySet()) {
            if (cases == null) {
                // 첫 번째 루프에서만 new CaseBuilder()로 시작
                cases = new CaseBuilder()
                        .when(meeting.meetingId.eq(entry.getKey()))
                        .then(entry.getValue());
            } else {
                // 두 번째부터는 기존 cases에 연결
                cases = cases.when(meeting.meetingId.eq(entry.getKey()))
                        .then(entry.getValue());
            }
        }

        // 모든 WHEN 절 뒤에 ELSE를 붙여서 최종 CASE 표현식 완성
        NumberExpression<Integer> seeCountExpression = cases.otherwise(meeting.seeCount);

        // 단 한 번의 UPDATE 쿼리 실행
        long executedCount = queryFactory
                .update(meeting)
                .set(meeting.seeCount, seeCountExpression) // 완성된 CASE 표현식 사용
                .where(meeting.meetingId.in(meetingViewCounts.keySet()))
                .execute();
    }

    @Override
    public List<MeetingHistoryResponse.MeetingHistoryList> getMeetingHistoryList(Long memberId, LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category) {
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        return queryFactory
                .select(Projections.constructor(MeetingHistoryResponse.MeetingHistoryList.class,
                        meeting.meetingId,
                        meeting.title,
                        meeting.date,
                        meeting.time,
                        meeting.category,
                        meeting.seeCount
                ))
                .from(meeting)
                .join(meeting.participants, participant)
                .where(
                        dateTimeCondition(lastMeetingDate, lastMeetingTime, lastMeetingId),
                        categoryEq(category),
                        participantCondition(memberId),
                        pastMeetingCondition(nowDate, nowTime)
                )
                .orderBy(meeting.date.desc(), meeting.time.desc(), meeting.meetingId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public MeetingValidationContext validateParticipantRequest(Long memberId, Long meetingId) {
        return queryFactory
                .select(Projections.constructor(MeetingValidationContext.class,
                        meeting.meetingId,
                        meeting.date,
                        meeting.time,
                        participant.participantId,
                        participant.status
                ))
                .from(meeting)
                .leftJoin(participant)
                .on(
                        participant.meeting.meetingId.eq(meeting.meetingId)
                                .and(participant.member.memberId.eq(memberId))
                )
                .where(meeting.meetingId.eq(meetingId))
                .fetchOne();
    }

    // memberId 가 승인 된 조건
    private BooleanExpression participantCondition(Long memberId) {
        if (memberId == null) {
            return null;
        }
        return participant.member.memberId.eq(memberId)
                .and(participant.status.eq(APPROVED));
    }

    // 날짜 시간이 지난 모임 조건 
    private BooleanExpression pastMeetingCondition(LocalDate nowDate, LocalTime nowTime) {
        if (nowDate == null || nowTime == null) {
            return null;
        }
        return meeting.date.before(nowDate)
                .or(meeting.date.eq(nowDate).and(meeting.time.before(nowTime)));
    }

    private BooleanExpression dateTimeCondition(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId) {
        if (lastMeetingDate == null || lastMeetingTime == null || lastMeetingId == null) {
            return null;
        }

        return meetingDateLt(lastMeetingDate) // 날짜가 더 이전이거나 meeting.date < lastMeetingDate
                .or(meetingDateEq(lastMeetingDate)
                        .and(meetingTimeLt(lastMeetingTime))) // 같은 날짜지만 시간이 더 이전이거나
                .or(meetingDateEq(lastMeetingDate)
                        .and(meetingTimeEq(lastMeetingTime))
                        .and(meetingIdLt(lastMeetingId))); // 날짜/시간까지 같으면 meetingId로 정렬 보장
    }

    private BooleanExpression meetingIdEq(Long meetingId) {
        return meeting.meetingId.eq(meetingId);
    }

    private BooleanExpression meetingIdLt(Long meetingId) {
        return meeting.meetingId.lt(meetingId);
    }

    private BooleanExpression meetingMemberIdEq(Long memberId) {
        return meeting.member.memberId.eq(memberId);
    }

    private BooleanExpression meetingDateEq(LocalDate date) {
        return meeting.date.eq(date);
    }

    private BooleanExpression meetingDateLt(LocalDate date) {
        return meeting.date.lt(date);
    }

    private BooleanExpression meetingTimeEq(LocalTime time) {
        return meeting.time.eq(time);
    }

    private BooleanExpression meetingTimeLt(LocalTime time) {
        return meeting.time.lt(time);
    }

    private BooleanExpression categoryEq(Category category) {
        return category != null ? meeting.category.eq(category) : null;
    }
}
