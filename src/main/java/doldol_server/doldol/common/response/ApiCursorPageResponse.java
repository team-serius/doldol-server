package doldol_server.doldol.common.response;

import java.util.List;

import doldol_server.doldol.common.dto.CursorPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ApiCursorPageResponse<T> {

	@Schema(description = "데이터")
	private final List<T> data;

	@Schema(description = "다음 커서")
	private final Long nextCursor;

	@Schema(description = "다음 데이터 존재 여부")
	private final boolean hasNext;

	@Schema(description = "총 데이터 개수 - 현재 페이지가 아닌 전체 데이터 개수")
	private final int totalPages;

	@Schema(description = "응답 코드", example = "200")
	private final int code;

	@Schema(description = "응답 메세지", example = "OK")
	private final String message;

	public ApiCursorPageResponse(CursorPage<T> data, int code, String message) {
		this.data = data.getData();
		this.nextCursor = data.getNextCursor();
		this.hasNext = data.isHasNext();
		this.totalPages = data.getData().size();
		this.code = code;
		this.message = message;
	}

	public static <T> ApiCursorPageResponse<T> ok(CursorPage<T> data) {
		return new ApiCursorPageResponse<>(data, 200, "OK");
	}
}
