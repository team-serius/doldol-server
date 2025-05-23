package doldol_server.doldol.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    // 400
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "C-001", "입력값이 올바르지 않습니다."),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "C-002", "잘못된 인자입니다."),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C-003", "서버 내부 오류가 발생했습니다."),
    RUNTIME_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C-004", "런타임 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}