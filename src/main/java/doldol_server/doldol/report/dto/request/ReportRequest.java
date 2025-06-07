package doldol_server.doldol.report.dto.request;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "ReportRequest: 신고 내역 요청 Dto")
public record ReportRequest(
	@NotNull(message = "메세지 ID는 필수입니다.")
	@Schema(description = "메세지 ID", example = "1")
	Long messageId,

	@NotBlank(message = "메세지 제목은 필수입니다.")
	@Schema(description = "제목", example = "신고합니다.")
	String title,

	@NotBlank(message = "메세지 내용은 필수입니다.")
	@Schema(description = "내용", example = "김돌돌씨를 신고합니다.")
	String content,

	@NotNull(message = "생성 날짜는 필수입니다.")
	@Schema(description = "생성 날짜", example = "2025-05-26T11:44:30.327959")
	LocalDateTime createdAt
) {
}
