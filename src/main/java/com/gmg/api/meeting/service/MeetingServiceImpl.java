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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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

        Map<Long, Long> acceptedCountMap = getAcceptedCountMapByMeetings(meetingList);
        return MeetingListResponse.of(meetingList, acceptedCountMap, hasNext);
    }

//    // 카페인 캐시 사용 (local Cache)
//    @Override
//    @Cacheable(cacheManager = "localCacheManager, cacheNames = "meetingDetailCache", key = "#meetingId")
//    public MeetingDetailStaticResponse getMeetingDetail(Long meetingId) {
//        Meeting meeting = getMeetingById(meetingId);
//        return MeetingDetailStaticResponse.of(meeting);
//    }


    @Override // Redis 캐싱에서 Meeting 본문 값 가져오는 메서드 (global Cache)
    public MeetingDetailStaticResponse getMeetingDetail(Long meetingId) {
        // 캐시에서 조회
        MeetingDetailStaticResponse.MeetingDetail meeting = meetingRedisService.getFromCache(meetingId);

        // 캐시에 없으면 DB 조회 후 캐싱
        if (meeting == null) {
            meeting = meetingRedisService.getFromDbAndCache(meetingId);
        }

        return MeetingDetailStaticResponse.of(meeting);
    }

    @Override
    @Transactional // Redis Cache에서 조회수 값 가져오기
    public SeeCountResponse updateMeetingViews(Long meetingId) {
        meetingRedisService.validateAndCacheSeeCount(meetingId); // Cache에 데이터 있는지 검증 없으면 세팅
        Long updatedCount = meetingRedisService.incrementSeeCount(meetingId); // Redis 1증가하고 값 반환
        return SeeCountResponse.of(meetingId, updatedCount);
    }

    @Override
    public Meeting getMeetingById(Long meetingId) {
        return meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new ResourceAlreadyExistsException("존재하지 않는 모임입니다."));
    }

    @Override
    public Meeting getReferenceMeetingById(Long meetingId) {
        if(!meetingRepository.existsById(meetingId)){
            throw new MatchMissException("존재하지 않는 모임입니다.");
        }
        return meetingRepository.getReferenceById(meetingId);
    }

    @Override // 방장 여부 판단 boolean 반환 값
    public boolean validateMeetingOwner(Long meetingId, Long memberId) {
        return meetingRepository.existsByMeetingIdAndMember_MemberId(meetingId, memberId);
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
