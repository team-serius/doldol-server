package doldol_server.doldol.rollingPaper.repository.custom;

import java.util.List;

import doldol_server.doldol.rollingPaper.dto.response.ParticipantResponse;

public interface ParticipantRepositoryCustom {
	boolean existsByUserAndPaper(Long userId, Long paperId);
	List<ParticipantResponse> findAllByPaperAndUserExceptMe(Long paperId, Long userId);
}
