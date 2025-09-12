package com.gmg.api.Participant.controller;

import com.gmg.api.ApiResponse;
import com.gmg.api.Participant.service.ParticipantService;
import com.gmg.global.oauth.jwt.dto.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
