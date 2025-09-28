package com.gmg.api.review.repository;

import com.gmg.api.review.domain.entity.Review;
import com.gmg.api.review.domain.response.dto.RevieweeDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT new com.gmg.api.review.domain.response.dto.RevieweeDto(r.reviewId, r.comment) FROM Review r WHERE r.reviewee.memberId = :memberId")
    List<RevieweeDto> getRevieweeList (@Param("memberId") Long memberId);

}
