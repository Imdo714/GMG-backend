package com.gmg.api.member.service;

import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.domain.request.LoginDto;
import com.gmg.api.member.domain.request.SingUpDto;
import com.gmg.api.member.domain.response.LoginResponse;
import com.gmg.api.member.repository.MemberRepository;
import com.gmg.api.member.service.async.MemberAsyncService;
import com.gmg.global.exception.handelException.MatchMissException;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import com.gmg.global.oauth.customHandler.info.OAuth2UserInfo;
import com.gmg.global.oauth.jwt.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
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

    @Override
    public LoginResponse loginForm(LoginDto loginDto) {
        Member member = getByEmailMember(loginDto.getEmail());
        matchPassword(member, loginDto.getPassword());

        return LoginResponse.of(jwtTokenProvider.createToken(member.getEmail(), member.getMemberId()), member);
    }

    @Override
    public LoginResponse GenerateAccessToken(OAuth2User principal) {
        Member member = getByEmailMember(principal.getAttribute("email"));
        return LoginResponse.of(jwtTokenProvider.createToken(member.getEmail(), member.getMemberId()), member);
    }

    @Override
    public Member getMemberById(Long memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MatchMissException("해당 사용자는 존재하지 않습니다."));
    }

    @Override
    public Member getReferenceMemberById(Long memberId) {
        try {
            return memberRepository.getReferenceById(memberId);
        } catch (EntityNotFoundException e) {
            throw new MatchMissException("해당 사용자는 존재하지 않습니다.");
        }
    }

    private Member getByEmailMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MatchMissException("해당 사용자는 존재하지 않습니다."));
    }

    private void matchPassword(Member member, String password) {
        if (!member.isPasswordMatch(password)) {
            throw new MatchMissException("비밀번호가 일치하지 않습니다.");
        }
    }

    private void isEmailAvailable(String email) {
        if(memberRepository.existsByEmail(email)){
            throw new ResourceAlreadyExistsException("이미 있는 이메일입니다.");
        }
    }

}
