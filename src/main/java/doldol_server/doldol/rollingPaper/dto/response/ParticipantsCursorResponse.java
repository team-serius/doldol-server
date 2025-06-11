package doldol_server.doldol.rollingPaper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ParticipantsCursorResponse(
	@Schema(description = "다음 페이지 요청을 위한 커서 이름", example = "김돌돌")
	String cursorName,

	@Schema(description = "다음 페이지 요청을 위한 커서 ID", example = "10")
	Long cursorId
) {
}
