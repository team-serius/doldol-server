package doldol_server.doldol.common.exception.errorCode;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageErrorCode implements ErrorCode {

	// 404
	MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "M-001", "메시지를 찾을 수 없습니다."),

	// 403
	MESSAGE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "M-002", "메시지에 접근할 권한이 없습니다."),

	// 400
	MESSAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "M-003", "메시지는 최대 5개까지만 작성할 수 있습니다."),

	// 409
	MESSAGE_ALREADY_EXISTS(HttpStatus.CONFLICT, "M-004", "이미 해당 사용자에게 보낸 메시지가 존재합니다.")
	;

	private HttpStatus httpStatus;
	private String code;
	private String message;
}