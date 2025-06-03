package doldol_server.doldol.rollingPaper.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.rollingPaper.repository.custom.PaperRepositoryCustom;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long>, PaperRepositoryCustom {
	Optional<Paper> findByInvitationCode(String invitationCode);

	@Query("select count(p) from Participant p where p.user.id = :userId")
	int countByUserId(Long userId);
}
