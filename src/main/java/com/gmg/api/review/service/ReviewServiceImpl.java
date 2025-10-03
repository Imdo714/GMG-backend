package com.gmg.api.review.service;

import com.gmg.api.Participant.repository.ParticipantRepository;
import com.gmg.api.meeting.service.MeetingService;
import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.service.MemberService;
import com.gmg.api.review.domain.entity.Review;
import com.gmg.api.review.domain.request.ReviewCommentDto;
import com.gmg.api.review.domain.response.RevieweeListResponse;
import com.gmg.api.review.domain.response.dto.ReviewCheckInfo;
import com.gmg.api.review.domain.response.dto.RevieweeDto;
import com.gmg.api.review.repository.ReviewRepository;
import com.gmg.global.exception.handelException.MatchMissException;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MeetingService meetingService;
    private final MemberService memberService;
    private final ParticipantRepository participantRepository;

    @Override
    @Transactional
    public void createReview(Long meetingId, Long targetMemberId, Long writerMemberId, ReviewCommentDto reviewCommentDto) {
        ReviewCheckInfo reviewInfo = participantRepository.getReviewCheckInfo(meetingId, writerMemberId, targetMemberId)
                .orElseThrow(() -> new MatchMissException("참여하지 않은 모임입니다."));
        validateReviewCheckInfoException(reviewInfo);

        Review review = Review.builder()
            .meeting(meetingService.getReferenceMeetingById(meetingId))
            .reviewer(getReferenceMemberById(writerMemberId))
            .reviewee(getReferenceMemberById(targetMemberId))
            .comment(reviewCommentDto.getComment())
            .createDate(LocalDate.now())
            .build();

        try {
            reviewRepository.save(review);
        } catch (DataIntegrityViolationException e) { // SQL 유니크 값 예외 확인 증복되면 예외 터짐
            throw new ResourceAlreadyExistsException("이미 리뷰를 작성하셨습니다.");
        }
    }

    @Override
    public RevieweeListResponse getRevieweeListByMemberId(Long memberId) {
        List<RevieweeDto> revieweeCount = reviewRepository.getRevieweeList(memberId);
        return RevieweeListResponse.builder()
                .list(revieweeCount)
                .count(revieweeCount.size())
                .build();
    }

    private Member getReferenceMemberById(Long memberId) {return memberService.getReferenceMemberById(memberId);}

    public void validateReviewCheckInfoException(ReviewCheckInfo info) {
        if (info.isNotBothParticipants()) {
            throw new ResourceAlreadyExistsException("같은 모임 참가자가 아닙니다.");
        }
        if (info.isBeforeMeetingEnd()) {
            throw new ResourceAlreadyExistsException("모임이 끝나야 리뷰를 작성할 수 있습니다.");
        }
    }
}
