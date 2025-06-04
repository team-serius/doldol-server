package doldol_server.doldol.rollingPaper.repository.custom;

import doldol_server.doldol.rollingPaper.entity.Message;

public interface MessageRepositoryCustom {

	Message getMessage(Long messageId, Long userId);
}
