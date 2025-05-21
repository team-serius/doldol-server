package doldol_server.doldol.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ApiResponse<T> {
	@Schema(description = "성공 응답에 대한 데이터")
	private final T data;
	@Schema(description = "HTTP 상태코드", example = "200")
	private final int status;
	@Schema(description = "요청 성공 메세지", example = "OK")
	private final String message;

	private ApiResponse(T data, int status, String message) {
		this.data = data;
		this.status = status;
		this.message = message;
	}

	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>(data, 200, "OK");
	}

	public static <T> ApiResponse<T> created(T data) {
		return new ApiResponse<>(data, 201, "CREATED");
	}

	public static ApiResponse<Void> noContent() {
		return new ApiResponse<>(null, 204, "NO_CONTENT");
	}
}
