package com.gmg.api.meeting.controller;

import com.gmg.api.ApiResponse;
import com.gmg.api.meeting.domain.request.CreateMeetingDto;
import com.gmg.api.meeting.domain.response.CreateMeetingResponse;
import com.gmg.api.meeting.service.MeetingService;
import com.gmg.global.oauth.jwt.dto.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

}
