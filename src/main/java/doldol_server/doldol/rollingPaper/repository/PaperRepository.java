package doldol_server.doldol.rollingPaper.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import doldol_server.doldol.rollingPaper.entity.Paper;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {
	Optional<Paper> findByInvitationCode(String invitationCode);
}
