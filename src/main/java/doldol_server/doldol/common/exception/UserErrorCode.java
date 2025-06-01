package doldol_server.doldol.common.exception;

import org.springframework.http.HttpStatus;

import doldol_server.doldol.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U-001", "회원을 찾을 수 없습니다."),
	;

	private HttpStatus httpStatus;
	private String code;
	private String message;
}
