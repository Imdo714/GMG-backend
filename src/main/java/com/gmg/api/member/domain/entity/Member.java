package com.gmg.api.member.domain.entity;

import com.gmg.api.Participant.domain.entity.Participant;
import com.gmg.api.member.domain.request.SingUpDto;
import com.gmg.api.meeting.domain.entity.Meeting;
import com.gmg.api.review.domain.entity.Review;
import com.gmg.api.util.PasswordEncoderUtil;
import com.gmg.global.oauth.customHandler.info.OAuth2UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MEMBER")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "profile")
    private String profile;

    @Column(name = "name")
    private String name;

    // 양방향 매핑: 한 명의 멤버가 여러 모임 주최 가능
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meeting> meetings = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    // 내가 작성한 리뷰들
    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> writtenReviews = new ArrayList<>();

    // 내가 받은 리뷰들
    @OneToMany(mappedBy = "reviewee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> receivedReviews = new ArrayList<>();

    public static Member singUpBuilder(SingUpDto singUpDto){
        return Member.builder()
                .email(singUpDto.getEmail())
                .password(PasswordEncoderUtil.encode(singUpDto.getPassword()))
                .name(singUpDto.getName())
                .profile("https://i.pravatar.cc/80?img=2") // 임시 기본 프로필
                .build();
    }

    public static Member SocialSingUpBuilder(OAuth2UserInfo oAuth2UserInfo) {
        return Member.builder()
                .email(oAuth2UserInfo.getEmail())
                .password(PasswordEncoderUtil.encode(oAuth2UserInfo.getProviderId() + oAuth2UserInfo.getProvoder())) // 소셜 + 소셜아이디 ex) google-1110110
                .name(oAuth2UserInfo.getNickname())
                .profile("https://i.pravatar.cc/80?img=2") // 임시 기본 프로필
                .build();
    }

    public boolean isPasswordMatch(String rawPassword) {
        return PasswordEncoderUtil.matches(rawPassword, this.password);
    }
}
