package doldol_server.doldol.rollingPaper.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PaperListResponse: 롤링페이퍼 리스트 응답 Dto")
public record PaperListResponse(
	@Schema(description = "총 페이퍼 수", example = "8")
	int paperCount,

	List<PaperResponse> rollingPaper
) {
}
