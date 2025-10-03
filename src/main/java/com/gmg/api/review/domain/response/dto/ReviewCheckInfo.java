package com.gmg.api.review.domain.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class ReviewCheckInfo {

    private LocalDate meetingDate;
    private LocalTime meetingTime;
    private long participantCount;

    public boolean isNotBothParticipants(){
        return participantCount != 2;
    }

    public boolean isBeforeMeetingEnd(){
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return meetingDate.isAfter(today) ||
                (meetingDate.isEqual(today) && meetingTime.isAfter(now));
    }


}
