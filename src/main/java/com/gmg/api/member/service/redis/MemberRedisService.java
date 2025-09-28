package com.gmg.api.member.service.redis;

import com.gmg.api.member.domain.response.dto.MyPageProfileInfoDto;
import com.gmg.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MemberRedisService {

    private static final String MEMBER_INFO_PREFIX = "member:info:";
    private static final Integer TTL_MINUTE_MEMBER_INFO_CACHE = 10;

    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberService memberService;

    public MyPageProfileInfoDto getMemberInfoCache(Long memberId){
        MyPageProfileInfoDto profileInfoDto = (MyPageProfileInfoDto) redisTemplate.opsForValue().get(MEMBER_INFO_PREFIX + memberId);
        if(profileInfoDto == null){
            profileInfoDto = getPageProfileInfoDto(memberId);
        }

        return profileInfoDto;
    }

    private MyPageProfileInfoDto getPageProfileInfoDto(Long memberId) {
        MyPageProfileInfoDto profileInfoDto = memberService.getMemberEmailAndName(memberId);
        redisTemplate.opsForValue().set(MEMBER_INFO_PREFIX + memberId, profileInfoDto, Duration.ofMinutes(TTL_MINUTE_MEMBER_INFO_CACHE));
        return profileInfoDto;
    }

}
