package doldol_server.doldol.rollingPaper.dto.response;

import java.time.LocalDate;

import doldol_server.doldol.rollingPaper.entity.Paper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "PaperDetailResponse: 롤링페이퍼 개별 조회 응답 Dto")
public record PaperDetailResponse(

	@Schema(description = "롤링페이퍼 ID", example = "1")
	Long paperId,

	@Schema(description = "단체 이름", example = "[KB] IT's Your Life 6기 16회차")
	String name,

	@Schema(description = "단체 설명", example = "KB 16회차 짱짱맨 영원하라.")
	String description,

	@Schema(description = "참여 인원", example = "28")
	int participantsCount,

	@Schema(description = "작성된 총 메세지 개수", example = "100")
	int messageCount,

	@Schema(description = "메세지 공개 날짜", example = "2025-06-26")
	LocalDate openDate,

	@Schema(description = "방장 여부", example = "true")
	boolean isMaster,

	@Schema(description = "초대 코드", example = "asdfls393ds")
	String code
) {
	public static PaperDetailResponse of(Paper paper, boolean isMaster) {
		return PaperDetailResponse.builder()
			.paperId(paper.getId())
			.name(paper.getName())
			.description(paper.getDescription())
			.participantsCount(paper.getParticipantsCount())
			.messageCount(paper.getMessageCount())
			.openDate(paper.getOpenDate())
			.isMaster(isMaster)
			.code(paper.getInvitationCode())
			.build();
	}
}

