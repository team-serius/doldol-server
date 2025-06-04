package doldol_server.doldol.rollingPaper.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import doldol_server.doldol.rollingPaper.entity.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
	@Query("select p "
		+ "from Participant p "
		+ "join fetch p.paper pa "
		+ "join fetch p.user u "
		+ "where pa.id = :paperId")
	List<Participant> findByIdWithUser(Long paperId);
}
