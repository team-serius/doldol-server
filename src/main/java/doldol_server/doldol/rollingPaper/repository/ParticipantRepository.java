package doldol_server.doldol.rollingPaper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import doldol_server.doldol.rollingPaper.entity.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
