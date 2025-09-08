package com.gmg.api.meeting.domain.entity;

import com.gmg.api.Participant.domain.entity.Participant;
import com.gmg.api.meeting.domain.request.CreateMeetingDto;
import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.review.domain.entity.Review;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MEETING")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long meetingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @Column(name = "address")
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "date")
    private Date date;

    @Column(name = "time")
    private LocalTime time;

    @Column(name = "person_count")
    private Integer personCount;

    @Column(name = "see_count")
    private Integer seeCount;

    @Column(name = "image")
    private String image;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public static Meeting of(Member member, CreateMeetingDto dto){
        return Meeting.builder()
                .member(member)
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(dto.getCategory())
                .address(dto.getAddress())
                .addressDetail(dto.getAddressDetail())
                .date(dto.getDate())
                .time(dto.getTime())
                .personCount(dto.getPersonCount())
                .image("https://i.pravatar.cc/80?img=5") // 임시 이미지
                .build();
    }
}
