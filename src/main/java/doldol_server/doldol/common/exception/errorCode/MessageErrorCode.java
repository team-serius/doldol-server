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
	;

	private HttpStatus httpStatus;
	private String code;
	private String message;
}