package com.gmg.api.member.repository;

import com.gmg.api.member.domain.entity.Member;
import com.gmg.api.member.domain.response.dto.MyPageProfileInfoDto;
import com.gmg.api.member.repository.queryDsl.MemberQueryDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryDslRepository {

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByMemberId(Long memberId);

    @Query("SELECT new com.gmg.api.member.domain.response.dto.MyPageProfileInfoDto(m.memberId, m.email, m.name, m.profile) FROM Member m WHERE m.memberId = :memberId")
    Optional<MyPageProfileInfoDto> getMemberEmailAndName(@Param("memberId") Long memberId);

}
