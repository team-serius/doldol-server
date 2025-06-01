package doldol_server.doldol.common.exception.errorCode;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaperErrorCode implements ErrorCode {
	PAPER_NOT_FOUND(HttpStatus.NOT_FOUND, "P-001", "롤링페이퍼를 찾을 수 없습니다."),
	PARTICIPANT_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "P-002", "롤링페이퍼에 사용자가 이미 존재합니다."),

	;

	private HttpStatus httpStatus;
	private String code;
	private String message;
}
