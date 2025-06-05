package doldol_server.doldol.common.exception.errorCode;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportErrorCode implements ErrorCode {
	REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "P-001", "해당 신고 내역을 찾을 수 없습니다."),
	REPORT_FORBIDDIN(HttpStatus.FORBIDDEN, "P-002", "해당 신고 내역에 접근할 권한이 없습니다.");

	private HttpStatus httpStatus;
	private String code;
	private String message;
}
