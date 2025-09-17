package doldol_server.doldol.rollingPaper.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.rollingPaper.repository.custom.PaperRepositoryCustom;
import feign.Param;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long>, PaperRepositoryCustom {
	Optional<Paper> findByInvitationCode(String invitationCode);

	@Query("select count(p) from Participant p where p.user.id = :userId")
	int countByUserId(Long userId);

	@Query("SELECT p.openDate FROM Paper p WHERE p.id = :paperId")
	Optional<LocalDate> findOpenDateById(@Param("paperId") Long paperId);
}
