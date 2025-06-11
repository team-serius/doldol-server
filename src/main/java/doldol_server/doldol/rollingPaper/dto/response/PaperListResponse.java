package doldol_server.doldol.rollingPaper.dto.response;

import doldol_server.doldol.common.dto.CursorPage;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PaperListResponse: 롤링페이퍼 리스트 응답 Dto")
public record PaperListResponse(
	@Schema(description = "총 페이퍼 수", example = "8")
	int paperCount,

	CursorPage<PaperResponse, Long> rollingPaper
) {
	public static PaperListResponse of(int paperCount, CursorPage<PaperResponse, Long> rollingPapers) {
		return new PaperListResponse(paperCount, rollingPapers);
	}
}
