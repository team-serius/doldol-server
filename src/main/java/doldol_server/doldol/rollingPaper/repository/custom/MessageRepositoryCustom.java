package doldol_server.doldol.rollingPaper.repository.custom;

import java.util.List;

import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.rollingPaper.dto.response.MessageResponse;
import doldol_server.doldol.rollingPaper.entity.Message;

public interface MessageRepositoryCustom {

	Message getMessage(Long messageId, Long userId);

	List<MessageResponse> getReceivedMessages(
		Long paperId,
		Long userId,
		CursorPageRequest request
	);

	List<MessageResponse> getSentMessages(
		Long paperId,
		Long userId,
		CursorPageRequest request
	);
}
