package doldol_server.doldol.rollingPaper.dto.response;

import java.time.LocalDateTime;

import doldol_server.doldol.rollingPaper.entity.Paper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "PaperResponse: 롤링페이퍼 생성 응답 Dto")
public record CreatePaperResponse(
	@Schema(description = "단체 이름", example = "[KB] IT's Your Life 6기 16회차")
	String name,

	@Schema(description = "단체 설명", example = "KB 16회차 짱짱맨 영원하라.")
	String description,

	@Schema(description = "메세지 공개 날짜", example = "2025-05-26T11:44:30.327959")
	LocalDateTime openDate,

	@Schema(description = "링크", example = "https://doldol.wha1eson.co.kr/paper?code=asdfls393ds")
	String link
) {
	public static CreatePaperResponse of(Paper paper, String defaultLink) {
		return CreatePaperResponse.builder()
			.name(paper.getName())
			.description(paper.getDescription())
			.openDate(paper.getOpenDate())
			.link(defaultLink + paper.getInvitationCode())
			.build();
	}
}
