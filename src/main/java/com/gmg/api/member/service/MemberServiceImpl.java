package com.gmg.api.member.service;

import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.domain.request.SingUpDto;
import com.gmg.api.member.repository.MemberRepository;
import com.gmg.api.member.service.async.MemberAsyncService;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import com.gmg.global.oauth.customHandler.info.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        isEmailAvailable(singUpDto.getEmail());

        Member member = Member.singUpBuilder(singUpDto);
        memberRepository.save(member); // 회원 저장
    }

    @Override
    @Transactional
    public Member socialLogin(OAuth2UserInfo oAuth2UserInfo) {
        Member member = Member.SocialSingUpBuilder(oAuth2UserInfo);
        return memberRepository.save(member); // 회원 저장
    }

    @Override
    public Optional<Member> securityFindByEmailMember(String email) {
        return memberRepository.findByEmail(email);
    }

    private void isEmailAvailable(String email) {
        if(memberRepository.existsByEmail(email)){
            throw new ResourceAlreadyExistsException("이미 있는 이메일입니다.");
        }
    }

}
