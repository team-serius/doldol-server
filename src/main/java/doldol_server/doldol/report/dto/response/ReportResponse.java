package doldol_server.doldol.report.dto.response;

import doldol_server.doldol.report.entity.Report;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ReportResponse: 신고 내역 응답 Dto")
public record ReportResponse(
	@Schema(description = "신고 ID", example = "1")
	Long reportId,

	@Schema(description = "메세지 ID", example = "1")
	Long messageId,

	@Schema(description = "메시지 내용", example = "넌 바보야")
	String messageContent,

	@Schema(description = "답변 여부", example = "false")
	boolean isAnswered
) {
	public static ReportResponse of(Report report) {
		return new ReportResponse(
			report.getId(),
			report.getMessage().getId(),
			report.getMessage().getContent(),
			report.getAnswer() != null
		);
	}
}
