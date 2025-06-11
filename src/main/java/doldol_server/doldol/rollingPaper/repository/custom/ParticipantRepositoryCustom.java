package doldol_server.doldol.rollingPaper.repository.custom;

import java.util.List;

import doldol_server.doldol.rollingPaper.dto.request.GetParticipantsRequest;
import doldol_server.doldol.rollingPaper.dto.response.ParticipantResponse;

public interface ParticipantRepositoryCustom {
	List<ParticipantResponse> getParticipants(Long paperId, GetParticipantsRequest pageRequest);
}
