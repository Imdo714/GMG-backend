package com.gmg.api.Participant.domain.entity;

import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.type.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static com.gmg.api.type.Status.APPROVED;
import static com.gmg.api.type.Status.PENDING;

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
    private Status status = PENDING;  // 기본값

    @Column(name = "join_date", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime joinDate = LocalDateTime.now();

//    Member ↔ Meeting : 1 : N (주최자 입장에서)
//    Member ↔ Participant ↔ Meeting : N : M (참여자 입장에서)

    public static Participant ofLeader(Member member, Meeting meeting){
        return Participant.builder()
                .member(member)
                .meeting(meeting)
                .status(APPROVED)
                .joinDate(LocalDateTime.now())
                .build();
    }

    public static Participant ofRequest(Member member, Meeting meeting){
        return Participant.builder()
                .meeting(meeting)
                .member(member)
                .status(PENDING)
                .joinDate(LocalDateTime.now())
                .build();
    }
}
