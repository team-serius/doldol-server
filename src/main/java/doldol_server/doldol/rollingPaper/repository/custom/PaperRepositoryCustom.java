package doldol_server.doldol.rollingPaper.repository.custom;

import java.util.List;

import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.common.request.SortDirection;
import doldol_server.doldol.rollingPaper.dto.response.PaperResponse;

public interface PaperRepositoryCustom {

	List<PaperResponse> getPapers(
		Long userId,
		CursorPageRequest request,
		SortDirection sortDirection
	);

	PaperResponse findPaperWithUserByInvitationCode(String invitationCode);
}
