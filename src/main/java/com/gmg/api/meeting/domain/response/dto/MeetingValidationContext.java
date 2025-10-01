package com.gmg.api.meeting.domain.response.dto;

import com.gmg.api.type.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class MeetingValidationContext {

    private Long meetingId;
    private LocalDate date;
    private LocalTime time;
    private Long participantId;
    private Status status;

    public boolean isAlreadyRejected() {
        return this.participantId != null && this.status == Status.REJECTED;
    }

    public boolean isExpired() {
        if (this.date == null || this.time == null) {
            return false; // 날짜나 시간이 없으면 만료되지 않은 것으로 간주
        }
        return LocalDateTime.of(this.date, this.time).isBefore(LocalDateTime.now());
    }
}
