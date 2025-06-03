package doldol_server.doldol.common.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CursorPageRequest(
	@Schema(description = "커서 페이징 기준 ID - null인 경우 첫 페이지로 간주합니다.", type = "long", example = "1")
	Long cursorId,

	@Schema(description = "가져올 데이터 개수", example = "10")
	int size
) {
}
