package com.gmg.api.review.domain.entity;

import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.meeting.domain.entity.Meeting;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "REVIEW")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    // FK → Meeting
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    // FK → Reviewer (리뷰 작성자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Member reviewer;

    // FK → Reviewee (리뷰 대상자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private Member reviewee;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "create_date", columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate createDate = LocalDate.now();

//    Meeting ↔ Review : 1 : N
//    Member ↔ Review (작성자) : 1 : N
//    Member ↔ Review (피리뷰자) : 1 : N
}
