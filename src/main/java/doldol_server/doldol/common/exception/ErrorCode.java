package doldol_server.doldol.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	/**
	 * 데이터 유효성 검사 실패
	 */
	INVALID_VALUE(HttpStatus.BAD_REQUEST, "D-001", "잘못된 요청입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
