package doldol_server.doldol.rollingPaper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "UpdateMessageRequest: 메세지 수정 Dto")
public record UpdateMessageRequest(
	@NotNull(message = "수정할 메세지의 ID가 입력되어야 합니다.")
	@Schema(description = "수정할 메세지 ID", example = "1")
	Long messageId,

	@NotBlank(message = "메세지 내용이 입력되어야 합니다.")
	@Schema(description = "메세지 내용", example = "가나다라마바사")
	String content,

	@NotBlank(message = "보내는 사람 이름이 입력되어야 합니다.")
	@Schema(description = "보내는 사람 이름", example = "돌돌")
	String from
) {
}
