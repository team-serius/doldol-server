package doldol_server.doldol.common.exception.errorCode;

import org.springframework.http.HttpStatus;

import doldol_server.doldol.common.exception.errorCode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {

	// 404
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U-001", "회원을 찾을 수 없습니다."),
	EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "U-002", "이메일을 찾을 수 없습니다."),
	ID_OR_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "U-003", "아이디 또는 이메일을 찾을 수 없습니다."),
	;

	private HttpStatus httpStatus;
	private String code;
	private String message;
}
