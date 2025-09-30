package com.gmg.api.meeting.service.command;

import com.gmg.api.Participant.domain.entity.Participant;
import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.domain.request.CreateMeetingDto;
import com.gmg.api.meeting.domain.response.CreateMeetingResponse;
import com.gmg.api.meeting.domain.response.SeeCountResponse;
import com.gmg.api.meeting.repository.MeetingRepository;
import com.gmg.api.meeting.service.MeetingService;
import com.gmg.api.meeting.service.redis.MeetingRedisService;
import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.service.MemberService;
import com.gmg.global.exception.handelException.MatchMissException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingCommandServiceImpl implements MeetingCommandService {

    private final MeetingRepository meetingRepository;
    private final MemberService memberService;
    private final MeetingService meetingService;
    private final MeetingRedisService meetingRedisService;

    // CQRS 패턴으로 Command 쓰기 영역
    // TODO : MeetingCommandServiceImpl 클래스 전체 튜닝, DB 쿼리 최대한 줄이면서 레이턴시 줄이기, SOLID 최대한 지키기

    @Override
    @Transactional
    public CreateMeetingResponse createMeeting(Long memberId, CreateMeetingDto createMeetingDto, MultipartFile image) {
        Member member = getReferenceMemberById(memberId);
        // TODO: 이미지 S3에 저장, S3 구축 되면 비동기적으로 저장할 예정

        Meeting meeting = Meeting.of(member, createMeetingDto); // 모임 생성
        Participant participant = Participant.ofLeader(member, meeting); // 모임 생성한 사람을 참가자에 넣음
        meeting.addParticipant(participant);

        return CreateMeetingResponse.of(meetingRepository.save(meeting));
    }

    @Override
    @Transactional
    public String deleteMeeting(Long meetingId, Long memberId) {
        validateMeetingOwnerBoolean(meetingId, memberId); // 로그인한 사람과 모임 생성자가 다른지 검증
        meetingRedisService.deleteCacheMeeting(meetingId); // Cache에 있으면 삭제
        meetingRepository.deleteById(meetingId); // CascadeType.ALL 설정으로 관련 FK 자동으로 삭제 (Participant, review)
        return "삭제 성공";
    }

    @Override
    @Transactional
    public SeeCountResponse increaseViews(Long meetingId) {
        meetingRedisService.validateAndCacheSeeCount(meetingId); // Cache에 데이터 있는지 검증 없으면 세팅
        Long updatedCount = meetingRedisService.incrementSeeCount(meetingId); // Redis 1증가하고 값 반환
        return SeeCountResponse.of(meetingId, updatedCount);
    }

    private void validateMeetingOwnerBoolean(Long meetingId, Long memberId) {
        if (!meetingService.validateMeetingOwner(meetingId, memberId)) {
            throw new MatchMissException("방장만 권한이 있습니다.");
        }
    }

    private Member getReferenceMemberById(Long memberId) {
        return memberService.getReferenceMemberById(memberId);
    }
}
