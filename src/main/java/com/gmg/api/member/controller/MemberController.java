package com.gmg.api.member.controller;

import com.gmg.api.member.domain.request.SingUpDto;
import com.gmg.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/singUpForm")
    public ResponseEntity<String> singUpForm(@RequestBody SingUpDto singUpDto){
        memberService.singUpForm(singUpDto);
        return ResponseEntity.ok("ok");
    }

}
