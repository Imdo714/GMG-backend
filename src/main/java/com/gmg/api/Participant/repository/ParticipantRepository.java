package com.gmg.api.Participant.repository;

import com.gmg.api.Participant.domain.entity.Participant;
import com.gmg.api.Participant.repository.queryDsl.ParticipantQueryDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long>, ParticipantQueryDslRepository {

    @Query("SELECT p.member.memberId FROM Participant p WHERE p.participantId = :participantId")
    Optional<Long> getRequestParticipantMemberId(@Param("participantId") Long participantId);
}
