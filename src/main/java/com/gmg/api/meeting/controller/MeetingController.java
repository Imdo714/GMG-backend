package com.gmg.api.meeting.controller;

import com.gmg.api.ApiResponse;
import com.gmg.api.Participant.domain.response.HistoryParticipant;
import com.gmg.api.meeting.domain.request.CreateMeetingDto;
import com.gmg.api.meeting.domain.response.*;
import com.gmg.api.meeting.service.command.MeetingCommandService;
import com.gmg.api.meeting.service.query.MeetingQueryService;
import com.gmg.api.type.Category;
import com.gmg.global.oauth.jwt.dto.CustomUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meeting")
public class MeetingController {

    // CQRS 패턴 적용
    private final MeetingCommandService meetingCommandService;
    private final MeetingQueryService meetingQueryService;

    @PostMapping
    public ApiResponse<CreateMeetingResponse> createMeeting(@AuthenticationPrincipal CustomUserPrincipal userPrincipal,
                                                            @Valid @RequestPart("data") CreateMeetingDto createMeetingDto,
                                                            @RequestPart(value = "image", required = false) MultipartFile image
    ){
        return ApiResponse.ok(meetingCommandService.createMeeting(userPrincipal.getMemberId(), createMeetingDto, image));
    }

    @GetMapping
    public ApiResponse<MeetingListResponse> meetingList(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastMeetingDate,
                                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime lastMeetingTime,
                                                        @RequestParam(required = false) Long lastMeetingId,
                                                        @RequestParam(defaultValue = "9") int size,
                                                        @RequestParam(required = false) Category category
    ){
        return ApiResponse.ok(meetingQueryService.getMeetingList(lastMeetingDate, lastMeetingTime, lastMeetingId, size, category));
    }

    @GetMapping("/{meetingId}")
    public ApiResponse<MeetingDetailStaticResponse> meetingDetail(@PathVariable Long meetingId){
        return ApiResponse.ok(meetingQueryService.getMeetingDetail(meetingId));
    }

    @PostMapping("/{meetingId}/views")
    public ApiResponse<SeeCountResponse> meetingDetailViews(@PathVariable Long meetingId){
        return ApiResponse.ok(meetingCommandService.increaseViews(meetingId));
    }

    @DeleteMapping("/{meetingId}")
    public ApiResponse<String> deleteMeeting(@PathVariable Long meetingId,
                                             @AuthenticationPrincipal CustomUserPrincipal userPrincipal
    ){
        return ApiResponse.ok(meetingCommandService.deleteMeeting(meetingId, userPrincipal.getMemberId()));
    }

    @PostMapping("/history") // 모임 내역 리스트
    public ApiResponse<MeetingHistoryResponse> meetingHistoryList(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastMeetingDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime lastMeetingTime,
            @RequestParam(required = false) Long lastMeetingId,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) Category category
    ){
        return ApiResponse.ok(meetingQueryService.getMeetingHistoryList(userPrincipal.getMemberId(), lastMeetingDate, lastMeetingTime, lastMeetingId, size, category));
    }

    @GetMapping("/history/particitpant/{meetingId}") // 참여했던 모임 참가자들
    public ApiResponse<HistoryParticipant> historyParticipant(
            @PathVariable Long meetingId
    ){
        return ApiResponse.ok(meetingQueryService.historyParticipant(meetingId));
    }

}
