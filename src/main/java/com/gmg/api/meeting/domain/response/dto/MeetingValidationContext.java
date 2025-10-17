package com.gmg.api.meeting.domain.response.dto;

import com.gmg.api.type.Status;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

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

    // 모임 신청 예외 여부 검증
    public void validate() {
        if (isExpired()) {
            throw new ResourceAlreadyExistsException("이미 만료된 모임입니다.");
        }

        if (isAlreadyApproved()) {
            throw new ResourceAlreadyExistsException("이미 참여 중인 모임입니다.");
        }

        if (isPending()) {
            throw new ResourceAlreadyExistsException("이미 신청 대기 중인 모임입니다.");
        }

        if (isAlreadyRejected()) {
            throw new ResourceAlreadyExistsException("이미 거절된 신청입니다.");
        }
    }

    public boolean isAlreadyRejected() {
        return this.participantId != null && this.status == Status.REJECTED;
    }

    public boolean isAlreadyApproved() {
        return this.participantId != null && this.status == Status.APPROVED;
    }

    public boolean isPending() {
        return this.participantId != null && this.status == Status.PENDING;
    }

    public boolean isExpired() {
        return LocalDateTime.of(this.date, this.time).isBefore(LocalDateTime.now());
    }
}
