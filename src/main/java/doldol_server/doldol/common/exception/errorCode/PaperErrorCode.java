package doldol_server.doldol.common.exception.errorCode;

import org.springframework.http.HttpStatus;

import doldol_server.doldol.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaperErrorCode implements ErrorCode {
	PAPER_NOT_FOUND(HttpStatus.NOT_FOUND, "P-001", "롤링페이퍼를 찾을 수 없습니다."),
	;

	private HttpStatus httpStatus;
	private String code;
	private String message;
}
