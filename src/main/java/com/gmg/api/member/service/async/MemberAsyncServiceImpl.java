package com.gmg.api.member.service.async;

import com.gmg.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MemberAsyncServiceImpl implements MemberAsyncService {

    private final MemberRepository memberRepository;

}
