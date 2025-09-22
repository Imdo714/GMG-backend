package com.gmg.api.Participant.controller;

import com.gmg.api.ApiResponse;
import com.gmg.api.Participant.domain.request.ParticipantIdDto;
import com.gmg.api.Participant.domain.response.ParticipantListResponse;
import com.gmg.api.Participant.service.ParticipantService;
import com.gmg.global.oauth.jwt.dto.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meeting/participant")
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping("/{meetingId}")
    public ApiResponse<String> participantRequest(@AuthenticationPrincipal CustomUserPrincipal userPrincipal,
                                                  @PathVariable Long meetingId){
        return ApiResponse.ok(participantService.participantRequest(userPrincipal.getMemberId(), meetingId));
    }

    @GetMapping("/{meetingId}")
    public ApiResponse<ParticipantListResponse> participantRequest(@PathVariable Long meetingId){
        return ApiResponse.ok(participantService.getParticipantList(meetingId));
    }

    @PostMapping("/{meetingId}/accepted")
    public ApiResponse<String> participantAccepted(@PathVariable Long meetingId,
                                                   @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
                                                   @RequestBody ParticipantIdDto participantIdDto
    ){
        return ApiResponse.ok(participantService.participantAccepted(meetingId, userPrincipal.getMemberId(), participantIdDto));
    }

    @PostMapping("/{meetingId}/reject")
    public ApiResponse<String> participantReject(@PathVariable Long meetingId,
                                                   @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
                                                   @RequestBody ParticipantIdDto participantIdDto
    ){
        return ApiResponse.ok(participantService.participantReject(meetingId, userPrincipal.getMemberId(), participantIdDto));
    }

    // 모임 취소 API 생성
    @DeleteMapping("/{meetingId}/cancel")
    public ApiResponse<String> participantCancel(@PathVariable Long meetingId,
                                                 @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
                                                 @RequestBody ParticipantIdDto participantIdDto
    ){
        return ApiResponse.ok(participantService.participantCancel(meetingId, userPrincipal.getMemberId(), participantIdDto));
    }
}
