package doldol_server.doldol.report.dto.request;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "ReportRequest: 신고 내역 요청 Dto")
public record ReportRequest(
	@NotNull(message = "메세지 ID는 필수입니다.")
	@Schema(description = "메세지 ID", example = "1")
	Long messageId
) {
}
