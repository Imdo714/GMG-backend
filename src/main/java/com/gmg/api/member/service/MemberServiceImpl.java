package com.gmg.api.member.service;

import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.domain.request.SingUpDto;
import com.gmg.api.member.repository.MemberRepository;
import com.gmg.api.member.service.async.MemberAsyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberAsyncService memberAsyncService;

    @Override
    @Transactional
    public void singUpForm(SingUpDto singUpDto) {
        memberRepository.existsByEmail(singUpDto.getEmail()); // 이메일 검증
        Member member = Member.singUpBuilder(singUpDto);
        memberAsyncService.saveMember(member); // 저장은 비동기
    }

}
