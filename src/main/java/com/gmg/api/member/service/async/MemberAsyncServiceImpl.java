package com.gmg.api.member.service.async;

import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberAsyncServiceImpl implements MemberAsyncService {

    private final MemberRepository memberRepository;

    @Async
    @Override
    public void saveMember(Member member) {
        memberRepository.save(member);
    }
}
