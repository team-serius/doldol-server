package doldol_server.doldol.rollingPaper.dto.response;

import doldol_server.doldol.common.dto.CursorPage;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MessageListResponse: 메세지 리스트 응답 Dto")
public record MessageListResponse(
	@Schema(description = "총 메세지 수", example = "24")
	int messageCount,

	CursorPage<MessageResponse> message
) {
	public static MessageListResponse of(int messageCount, CursorPage<MessageResponse> message) {
		return new MessageListResponse(messageCount, message);
	}
}
