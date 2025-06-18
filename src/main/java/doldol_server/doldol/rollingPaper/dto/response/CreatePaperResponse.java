package doldol_server.doldol.rollingPaper.dto.response;

import java.time.LocalDate;

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

	@Schema(description = "메세지 공개 날짜", example = "2025-06-26")
	LocalDate openDate,

	@Schema(description = "초대 코드", example = "asdfls393ds")
	String code
) {
	public static CreatePaperResponse of(Paper paper) {
		return CreatePaperResponse.builder()
			.name(paper.getName())
			.description(paper.getDescription())
			.openDate(paper.getOpenDate())
			.code(paper.getInvitationCode())
			.build();
	}
}
