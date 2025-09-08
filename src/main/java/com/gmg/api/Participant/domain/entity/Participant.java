package com.gmg.api.Participant.domain.entity;

import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.meeting.domain.entity.Meeting;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PARTICIPANT")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;

    // FK → Meeting
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    // FK → Member
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;  // 기본값

    @Column(name = "join_date", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime joinDate = LocalDateTime.now();

//    Member ↔ Meeting : 1 : N (주최자 입장에서)
//    Member ↔ Participant ↔ Meeting : N : M (참여자 입장에서)

}
