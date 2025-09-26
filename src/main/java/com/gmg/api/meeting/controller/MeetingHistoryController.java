package com.gmg.api.meeting.controller;

import com.gmg.api.ApiResponse;
import com.gmg.api.Participant.domain.response.HistoryParticipant;
import com.gmg.api.meeting.domain.response.MeetingHistoryResponse;
import com.gmg.api.meeting.service.MeetingHistoryService;
import com.gmg.api.type.Category;
import com.gmg.global.oauth.jwt.dto.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meeting/history")
public class MeetingHistoryController {

    private final MeetingHistoryService meetingHistoryService;

    @PostMapping
    public ApiResponse<MeetingHistoryResponse> meetingHistoryList(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastMeetingDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime lastMeetingTime,
            @RequestParam(required = false) Long lastMeetingId,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) Category category
    ){
        return ApiResponse.ok(meetingHistoryService.getMeetingHistoryList(userPrincipal.getMemberId(), lastMeetingDate, lastMeetingTime, lastMeetingId, size, category));
    }

    @GetMapping("/particitpant/{meetingId}")
    public ApiResponse<HistoryParticipant> historyParticipant(
            @PathVariable Long meetingId
    ){
        return ApiResponse.ok(meetingHistoryService.historyParticipant(meetingId));
    }
}
