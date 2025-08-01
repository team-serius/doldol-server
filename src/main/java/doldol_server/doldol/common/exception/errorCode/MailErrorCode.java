package doldol_server.doldol.common.exception.errorCode;

import org.springframework.http.HttpStatus;

import doldol_server.doldol.common.exception.errorCode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailErrorCode implements ErrorCode {

	// 400
	MISSING_EMAIL(HttpStatus.BAD_REQUEST, "M-001", "이메일 주소가 누락되었습니다."),

	// 500
	EMAIL_SENDING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "M-002", "이메일 발송에 실패했습니다."),
	;

	private HttpStatus httpStatus;
	private String code;
	private String message;
}
