package com.gmg.api.Participant.repository;

import com.gmg.api.Participant.domain.entity.Participant;
import com.gmg.api.Participant.repository.queryDsl.ParticipantQueryDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long>, ParticipantQueryDslRepository {

}
