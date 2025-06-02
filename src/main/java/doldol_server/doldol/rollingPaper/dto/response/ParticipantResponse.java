package doldol_server.doldol.rollingPaper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ParticipantResponse: 롤링페이퍼 참여자 응답 Dto")
public record ParticipantResponse(
	@Schema(description = "참여 아이디", example = "1")
	Long participantId,

	@Schema(description = "참여 유저 아이디", example = "1")
	Long userId,
	@Schema(description = "참여자 이름", example = "김돌돌")
	String name
) {
}
