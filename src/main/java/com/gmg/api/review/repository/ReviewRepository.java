package com.gmg.api.review.repository;

import com.gmg.api.review.domain.entity.Review;
import com.gmg.api.review.domain.response.dto.RevieweeDto;
import com.gmg.api.review.repository.queryDsl.ReviewQueryDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewQueryDslRepository {

    @Query("SELECT new com.gmg.api.review.domain.response.dto.RevieweeDto(r.reviewId, r.comment) FROM Review r WHERE r.reviewee.memberId = :memberId")
    List<RevieweeDto> getRevieweeList (@Param("memberId") Long memberId);

}
