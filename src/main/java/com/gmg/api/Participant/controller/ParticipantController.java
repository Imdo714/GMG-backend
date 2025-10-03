package com.gmg.api.Participant.controller;

import com.gmg.api.ApiResponse;
import com.gmg.api.Participant.domain.request.ParticipantIdDto;
import com.gmg.api.Participant.domain.response.ParticipantListResponse;
import com.gmg.api.Participant.service.ParticipantService;
import com.gmg.api.Participant.service.command.ParticipantCommandService;
import com.gmg.api.Participant.service.query.ParticipantQueryService;
import com.gmg.global.oauth.jwt.dto.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meeting/participant")
public class ParticipantController {

    private final ParticipantCommandService participantCommandService;
    private final ParticipantQueryService participantQueryService;

    @PostMapping("/{meetingId}")
    public ApiResponse<String> participantRequest(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Long meetingId
    ){
        return ApiResponse.ok(participantCommandService.participantRequest(userPrincipal.getMemberId(), meetingId));
    }

    @GetMapping("/{meetingId}")
    public ApiResponse<ParticipantListResponse> participantList(@PathVariable Long meetingId){
        return ApiResponse.ok(participantQueryService.getParticipantList(meetingId));
    }

    @PostMapping("/{meetingId}/accepted")
    public ApiResponse<String> updateParticipantAccepted(@PathVariable Long meetingId,
                                                   @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
                                                   @RequestBody ParticipantIdDto participantIdDto
    ){
        return ApiResponse.ok(participantCommandService.updateParticipantAccepted(meetingId, userPrincipal.getMemberId(), participantIdDto));
    }

    @PostMapping("/{meetingId}/reject")
    public ApiResponse<String> updateParticipantReject(@PathVariable Long meetingId,
                                                   @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
                                                   @RequestBody ParticipantIdDto participantIdDto
    ){
        return ApiResponse.ok(participantCommandService.updateParticipantReject(meetingId, userPrincipal.getMemberId(), participantIdDto));
    }

    @DeleteMapping("/{meetingId}/cancel")
    public ApiResponse<String> participantCancel(@PathVariable Long meetingId,
                                                 @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
                                                 @RequestBody ParticipantIdDto participantIdDto
    ){
        return ApiResponse.ok(participantCommandService.updateParticipantCancel(meetingId, userPrincipal.getMemberId(), participantIdDto));
    }
}
