package doldol_server.doldol.rollingPaper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "DeleteMessageRequest: 메세지 삭제 Dto")
public record DeleteMessageRequest(
	@NotNull(message = "삭제할 메세지 ID가 입력되어야 합니다.")
	@Schema(description = "삭제할 메세지 ID", example = "1")
	Long messageId
) {
}
