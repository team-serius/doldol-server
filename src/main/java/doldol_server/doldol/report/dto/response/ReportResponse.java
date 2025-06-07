package doldol_server.doldol.report.dto.response;

import java.time.LocalDateTime;

import doldol_server.doldol.report.entity.Report;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ReportResponse: 신고 내역 응답 Dto")
public record ReportResponse(
	@Schema(description = "메세지 ID", example = "1")
	Long messageId,

	@Schema(description = "메시지 내용", example = "넌 바보야")
	String messageContent,

	@Schema(description = "제목", example = "신고합니다.")
	String title,

	@Schema(description = "내용", example = "김돌돌씨를 신고합니다.")
	String content,

	@Schema(description = "생성 날짜", example = "2025-05-26T11:44:30.327959")
	LocalDateTime createdAt,

	@Schema(description = "답변 여부", example = "false")
	boolean isAnswered
) {
	public static ReportResponse of(Report report) {
		return new ReportResponse(
			report.getMessage().getId(),
			report.getMessage().getContent(),
			report.getTitle(),
			report.getContent(),
			report.getCreatedAt(),
			report.getAnswer() != null
		);
	}
}
