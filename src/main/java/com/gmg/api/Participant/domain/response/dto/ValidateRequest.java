package com.gmg.api.Participant.domain.response.dto;

import com.gmg.api.type.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@ToString
@AllArgsConstructor
public class ValidateRequest {

    private Long meetingId;
    private LocalDate date;
    private LocalTime time;
    private Long participantId;
    private Status status;
}
