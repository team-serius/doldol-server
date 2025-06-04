package doldol_server.doldol.rollingPaper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CreateMessageRequest: 메세지 생성 Dto")
public record CreateMessageRequest(
	@NotNull(message = "롤링페이퍼 ID는 필수입니다.")
	@Schema(description = "롤링페이퍼 ID", example = "1")
	Long paperId,

	@NotNull(message = "받는 사람 ID가 입력되어야 합니다.")
	@Schema(description = "받는 사람 ID", example = "가나다라마바사")
	Long receiverId,

	@NotBlank(message = "메세지 내용이 입력되어야 합니다.")
	@Schema(description = "메세지 내용", example = "가나다라마바사")
	String content,

	@NotBlank(message = "보내는 사람 이름이 입력되어야 합니다.")
	@Schema(description = "보내는 사람 이름", example = "돌돌")
	String from,

	@Schema(description = "글씨체", example = "프리텐다드")
	String fontStyle,

	@Schema(description = "배경 색상", example = "#000000")
	String backgroundColor
) {
}
