package com.gmg.api.meeting.controller;

import com.gmg.api.ApiResponse;
import com.gmg.api.meeting.domain.request.CreateMeetingDto;
import com.gmg.api.meeting.domain.response.CreateMeetingResponse;
import com.gmg.api.meeting.domain.response.MeetingListResponse;
import com.gmg.api.meeting.service.MeetingService;
import com.gmg.global.oauth.jwt.dto.CustomUserPrincipal;
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

    private final MeetingService meetingService;

    @PostMapping
    public ApiResponse<CreateMeetingResponse> createMeeting(@AuthenticationPrincipal CustomUserPrincipal userPrincipal,
                                                            @RequestBody CreateMeetingDto createMeetingDto,
                                                            @RequestParam(value = "image", required = false) MultipartFile image
    ){
        return ApiResponse.ok(meetingService.createMeeting(userPrincipal.getMemberId(), createMeetingDto, image));
    }

    @GetMapping
    public ApiResponse<MeetingListResponse> meetingList(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastMeetingDate,
                                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime lastMeetingTime,
                                                        @RequestParam int size
    ){ // DB 에 date, time 컬럼을 인덱스 걸어서 조회 성능 최적화 하기
        return ApiResponse.ok(meetingService.getMeetingList(lastMeetingDate, lastMeetingTime, size));
    }


    // TODO: /posts/meeting/{id} 로 본문 가져와서 캐싱하고
    //  POST/posts/meeting/{id}/views 로 조회수 1증가 할 예정

}
