package doldol_server.doldol.rollingPaper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import doldol_server.doldol.rollingPaper.entity.Participant;
import doldol_server.doldol.rollingPaper.repository.custom.ParticipantRepositoryCustom;

public interface ParticipantRepository extends JpaRepository<Participant, Long>, ParticipantRepositoryCustom {
}
