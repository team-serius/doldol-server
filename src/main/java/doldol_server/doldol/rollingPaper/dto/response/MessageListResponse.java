package doldol_server.doldol.rollingPaper.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MessageListResponse: 메세지 리스트 응답 Dto")
public record MessageListResponse(
	@Schema(description = "메세지 갯수", example = "24")
	int messageCount,

	@Schema(description = "롤링페이퍼 url", example = "https://www.doldol.com/{uuid}")
	String url,

	@Schema(description = "관리자 여부", example = "true")
	boolean isMaster,

	List<MessageResponse> message
) {
}
