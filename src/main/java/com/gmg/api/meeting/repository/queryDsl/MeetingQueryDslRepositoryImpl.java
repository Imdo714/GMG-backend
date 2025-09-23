package com.gmg.api.meeting.repository.queryDsl;

import com.gmg.api.meeting.domain.entity.QMeeting;
import com.gmg.api.meeting.domain.response.MeetingDetailStaticResponse;
import com.gmg.api.meeting.domain.response.MeetingListResponse;
import com.gmg.api.type.Category;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MeetingQueryDslRepositoryImpl implements MeetingQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QMeeting meeting = QMeeting.meeting;

    @Override
    public List<MeetingListResponse.MeetingList> getMeetingList(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category) {
        return queryFactory
                .select(Projections.constructor(MeetingListResponse.MeetingList.class,
                        meeting.meetingId,
                        meeting.title,
                        meeting.date,
                        meeting.time,
                        meeting.category,
                        meeting.personCount,
                        meeting.seeCount,
                        Expressions.constant(0L)
                ))
                .from(meeting)
                .where(
                        dateTimeCondition(lastMeetingDate, lastMeetingTime, lastMeetingId),
                        categoryEq(category)
                )
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
