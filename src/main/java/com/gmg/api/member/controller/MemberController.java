package com.gmg.api.member.controller;

import com.gmg.api.ApiResponse;
import com.gmg.api.member.domain.request.LoginDto;
import com.gmg.api.member.domain.request.SingUpDto;
import com.gmg.api.member.domain.response.LoginResponse;
import com.gmg.api.member.domain.response.MyPageResponse;
import com.gmg.api.member.service.MemberService;
import com.gmg.api.member.service.myPage.MemberMyPage;
import com.gmg.global.oauth.jwt.dto.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private  final MemberMyPage memberMyPage;

    @PostMapping("/singUpForm")
    public ResponseEntity<String> singUpForm(@RequestBody SingUpDto singUpDto){
        memberService.singUpForm(singUpDto);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginDto loginDto){
        return ApiResponse.ok(memberService.loginForm(loginDto));
    }

    @GetMapping("/loginSuccess")
    public ApiResponse<LoginResponse> socialLoginSuccess(@AuthenticationPrincipal OAuth2User principal){
        return ApiResponse.ok(memberService.GenerateAccessToken(principal));
    }

    @GetMapping("/loginFailure")
    public ResponseEntity<String> loginFailure(@RequestParam String error) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Login failed: " + error);
    }

    // 마이페이지 부분 만들어야 함
    @PostMapping("/my-page")
    public ApiResponse<MyPageResponse> myPage(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal
    ){
        return ApiResponse.ok(memberMyPage.getMyPageMemberInfo(userPrincipal.getMemberId()));
    }
}
