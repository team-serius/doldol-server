package doldol_server.doldol.rollingPaper.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "MessageResponse: 메세지 응답 Dto")
public record MessageResponse(
	@NotBlank(message = "수신/발신 여부가 입력되어야 합니다.")
	@Schema(description = "수신/발신 여부", example = "RECEIVE/SENT")
	String toOrFrom,

	@NotBlank(message = "메세지 내용이 입력되어야 합니다.")
	@Schema(description = "메세지 내용", example = "가나다라마바사")
	String content,

	@NotBlank(message = "받은/보낸 사람 이름이 입력되어야 합니다.")
	@Schema(description = "받은/보낸 사람", example = "돌돌")
	String name,

	@NotBlank(message = "생성 날짜가 있어야 합니다.")
	@Schema(description = "생성 날짜", example = "2025-05-26T11:44:30.327959")
	LocalDateTime createdAt
) {
}
