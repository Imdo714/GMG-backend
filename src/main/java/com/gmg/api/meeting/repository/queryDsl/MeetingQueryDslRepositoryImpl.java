package com.gmg.api.meeting.repository.queryDsl;

import com.gmg.api.meeting.domain.entity.QMeeting;
import com.gmg.api.meeting.domain.response.MeetingListResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class MeetingQueryDslRepositoryImpl implements MeetingQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QMeeting meeting = QMeeting.meeting;

    @Override
    public List<MeetingListResponse.MeetingList> getMeetingList(LocalDate lastMeetingDate, LocalTime lastMeetingTime, int size) {
        return queryFactory
                .select(Projections.constructor(MeetingListResponse.MeetingList.class,
                        meeting.meetingId,
                        meeting.title,
                        meeting.date,
                        meeting.time,
                        meeting.category,
                        meeting.personCount))
                .from(meeting)
                .where(dateTimeCondition(lastMeetingDate, lastMeetingTime, meeting))
                .orderBy(meeting.date.desc(), meeting.time.desc())
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

    private BooleanExpression meetingIdEq(Long meetingId) {
        return meeting.meetingId.eq(meetingId);
    }

    private BooleanExpression meetingMemberIdEq(Long memberId) {
        return meeting.member.memberId.eq(memberId);
    }

    private BooleanExpression dateTimeCondition(LocalDate lastMeetingDate, LocalTime lastMeetingTime, QMeeting meeting) {
        if (lastMeetingDate == null || lastMeetingTime == null) {
            return null;
        }

        return meeting.date.lt(lastMeetingDate) // meeting.date < lastMeetingDate
                .or(meeting.date.eq(lastMeetingDate)
                        .and(meeting.time.lt(lastMeetingTime)));
    }

}
