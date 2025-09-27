package com.gmg.api.meeting.service;

import com.gmg.api.Participant.domain.entity.Participant;
import com.gmg.api.Participant.repository.ParticipantRepository;
import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.meeting.domain.request.CreateMeetingDto;
import com.gmg.api.meeting.domain.response.CreateMeetingResponse;
import com.gmg.api.meeting.domain.response.MeetingDetailStaticResponse;
import com.gmg.api.meeting.domain.response.MeetingListResponse;
import com.gmg.api.meeting.domain.response.SeeCountResponse;
import com.gmg.api.meeting.repository.MeetingRepository;
import com.gmg.api.meeting.service.redis.MeetingRedisService;
import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.service.MemberService;
import com.gmg.api.type.Category;
import com.gmg.global.exception.handelException.MatchMissException;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingRedisService meetingRedisService;
    private final MemberService memberService;
    // 무한 순환참조 방지를 위해 Repository사용, Participant는 Meeting이 필요한게 자연습럽지만 반대는 부자연스러움
    private final ParticipantRepository participantRepository;

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
    public MeetingListResponse getMeetingList(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category) {
        // Size + 1은 마지막 페이지인지 확인 하기 위해
        List<MeetingListResponse.MeetingList> meetingList = getMeetingListFetch(lastMeetingDate, lastMeetingTime, lastMeetingId, size + 1, category);
        boolean hasNext = meetingList.size() > size;

        markClosedMeetings(meetingList); // 날짜 시간 기준으로 마감 체크 
        Map<Long, Long> acceptedCountMap = getAcceptedCountMapByMeetings(meetingList); // 승인된 인원 분리 
        return MeetingListResponse.of(meetingList, acceptedCountMap, hasNext);
    }

    @Override // Redis 캐싱에서 Meeting 본문 값 가져오는 메서드 (global Cache)
    public MeetingDetailStaticResponse getMeetingDetail(Long meetingId) {
        // 캐시에서 조회
        MeetingDetailStaticResponse.MeetingDetail meeting = meetingRedisService.getFromCache(meetingId);

        // 캐시에 없으면 DB 조회 후 캐싱
        if (meeting == null) {
            meeting = meetingRedisService.getFromDbAndCache(meetingId);
        }

        boolean isClosed = LocalDateTime.of(meeting.getDate(), meeting.getTime()).isBefore(LocalDateTime.now());
        return MeetingDetailStaticResponse.of(meeting, isClosed);
    }

    @Override
    @Transactional // Redis Cache에서 조회수 값 가져오기
    public SeeCountResponse updateMeetingViews(Long meetingId) {
        meetingRedisService.validateAndCacheSeeCount(meetingId); // Cache에 데이터 있는지 검증 없으면 세팅
        Long updatedCount = meetingRedisService.incrementSeeCount(meetingId); // Redis 1증가하고 값 반환
        return SeeCountResponse.of(meetingId, updatedCount);
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
    public Meeting getMeetingById(Long meetingId) {
        return meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new ResourceAlreadyExistsException("존재하지 않는 모임입니다."));
    }

    @Override
    public Meeting getReferenceMeetingById(Long meetingId) {
        try {
            return meetingRepository.getReferenceById(meetingId);
        } catch (EntityNotFoundException e) {
            throw new MatchMissException("존재하지 않는 모임입니다.");
        }
    }

    @Override // 방장 여부 판단 boolean 반환 값
    public boolean validateMeetingOwner(Long meetingId, Long memberId) {
        return meetingRepository.existsByMeetingIdAndMember_MemberId(meetingId, memberId);
    }

    @Override
    public Long getMakeMeetingOwner(Long meetingId) {
        return meetingRepository.getMakeMeetingOwner(meetingId)
                .orElseThrow(() -> new ResourceAlreadyExistsException("존재하지 않는 모임입니다."));
    }

    // Meeting 리스트들 마감된 모임들만 체크해서 마감 처리
    private static void markClosedMeetings(List<MeetingListResponse.MeetingList> meetingList) {
        LocalDateTime now = LocalDateTime.now();
        for (MeetingListResponse.MeetingList meeting : meetingList) {
            LocalDateTime meetingDateTime = LocalDateTime.of(meeting.getDate(), meeting.getTime());
            if (meetingDateTime.isBefore(now)) { // 모임 날짜가 현재 날짜보다 이전이면
                meeting.updateStatus(true); // 마감됨
            } else {
                meeting.updateStatus(false); // 진행중
            }
        }
    }

    // memberId 와 meeting 생성자가 다르면 예외
    private void validateMeetingOwnerBoolean(Long meetingId, Long memberId) {
        if (!validateMeetingOwner(meetingId, memberId)) {
            throw new MatchMissException("방장만 권한이 있습니다.");
        }
    }

    // Meeting 리스트를 가져오는 메서드
    private List<MeetingListResponse.MeetingList> getMeetingListFetch(LocalDate lastMeetingDate, LocalTime lastMeetingTime, Long lastMeetingId, int size, Category category) {
        return meetingRepository.getMeetingList(lastMeetingDate, lastMeetingTime, lastMeetingId, size, category);
    }

    // Member 프록시 반환
    private Member getReferenceMemberById(Long memberId) {
        return memberService.getReferenceMemberById(memberId);
    }

    // meetingList 를 받아 안에있는 meetingId를 뽑아서 meetingId별 승인 인원이 몇명인지 반환
    private Map<Long, Long> getAcceptedCountMapByMeetings(List<MeetingListResponse.MeetingList> meetingList) {
        return participantRepository.getAcceptedCountsByMeetingIds(
                meetingList.stream()
                        .map(MeetingListResponse.MeetingList::getMeetingId)
                        .collect(Collectors.toList())
        );
    }

}
