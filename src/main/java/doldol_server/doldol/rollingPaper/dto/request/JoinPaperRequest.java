package doldol_server.doldol.rollingPaper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "JoinPaperRequest: 롤링페이퍼 초대 요청 Dto")
public record JoinPaperRequest(
	@NotNull(message = "롤링페이퍼 참여코드는 필수입니다.")
	@Schema(description = "롤링페이퍼 참여 코드", example = "1b10e79f-6511-47cd-89b4-7910460d81b5")
	String invitationCode
) {
}
