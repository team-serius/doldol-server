package doldol_server.doldol.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ErrorResponse<T> {

	@Schema(name = "에러가 발생한 내용")
	private final T data;
	@Schema(name = "에러 코드 [알파벳-숫자]", example = "D-001")
	private final String code;
	@Schema(name = "에러 메세지", example = "잘못된 요청입니다.")
	private final String message;

	private ErrorResponse(T data, String code, String message) {
		this.data = data;
		this.code = code;
		this.message = message;
	}

	public static ErrorResponse<Void> error(String code, String message) {
		return error(null, code, message);
	}

	public static <T> ErrorResponse<T> error(T data, String code, String message) {
		return new ErrorResponse<>(data, code, message);
	}

}
