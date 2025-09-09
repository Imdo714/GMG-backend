package com.gmg.api.meeting.repository.queryDsl;

import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.domain.entity.QMeeting;
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

    @Override
    public List<Meeting> getMeetingList(LocalDate lastMeetingDate, LocalTime lastMeetingTime, int size) {
        QMeeting meeting = QMeeting.meeting;

        BooleanExpression predicate = null;
        if (lastMeetingDate != null && lastMeetingTime != null) {
            predicate = meeting.date.lt(lastMeetingDate) // meeting.date < lastMeetingDate
                        .or(meeting.date.eq(lastMeetingDate)
                            .and(meeting.time.lt(lastMeetingTime)));
        }

        return queryFactory
                .selectFrom(meeting)
                .where(predicate)
                .orderBy(meeting.date.desc(), meeting.time.desc())
                .limit(size)
                .fetch();
    }


}
