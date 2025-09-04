package com.gmg.api.member.controller;

import com.gmg.api.member.domain.request.SingUpDto;
import com.gmg.api.member.service.MemberService;
import lombok.Getter;
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

    @PostMapping("/singUpForm")
    public ResponseEntity<String> singUpForm(@RequestBody SingUpDto singUpDto){
        memberService.singUpForm(singUpDto);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/loginSuccess")
    public ResponseEntity<String> loginSuccess(@AuthenticationPrincipal OAuth2User principal){ // 토큰 발급 할 예정
        String name = principal.getName();
        String email = principal.getAttribute("email");
        return ResponseEntity.ok("email=" + email + ", name=" + name);
    }

    @GetMapping("/loginFailure")
    public ResponseEntity<String> loginFailure(@RequestParam String error) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Login failed: " + error);
    }

}
