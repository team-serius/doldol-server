package doldol_server.doldol.rollingPaper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "JoinPaperRequest: 롤링페이퍼 초대 요청 Dto")
public record JoinPaperRequest(
	@NotNull(message = "롤링페이퍼 ID는 필수입니다.")
	@Schema(description = "롤링페이퍼 ID", example = "1")
	Long paperId
) {
}
