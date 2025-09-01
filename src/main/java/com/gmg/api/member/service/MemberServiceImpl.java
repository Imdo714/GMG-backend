package com.gmg.api.member.service;

import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.domain.request.SingUpDto;
import com.gmg.api.member.repository.MemberRepository;
import com.gmg.api.member.service.async.MemberAsyncService;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberAsyncService memberAsyncService;

    @Override
    @Transactional
    public void singUpForm(SingUpDto singUpDto) {
        isEmailAvailable(singUpDto); // 이메일 검증
        Member member = Member.singUpBuilder(singUpDto);
        memberRepository.save(member); // 회원 저장
    }

    private void isEmailAvailable(SingUpDto singUpDto) {
        boolean byEmail = memberRepository.existsByEmail(singUpDto.getEmail());
        if(byEmail){
            throw new ResourceAlreadyExistsException("이미 있는 이메일입니다.");
        }
    }

}
