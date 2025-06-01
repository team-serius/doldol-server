package doldol_server.doldol.rollingPaper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.rollingPaper.entity.Participant;
import doldol_server.doldol.user.entity.User;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
	boolean existsByUserAndPaper(User user, Paper paper);
}
