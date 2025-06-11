package doldol_server.doldol.rollingPaper.repository.custom;

import java.util.List;

import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.rollingPaper.entity.Participant;

public interface ParticipantRepositoryCustom {
	List<Participant> getParticipants(Long paperId, CursorPageRequest pageRequest);
}
