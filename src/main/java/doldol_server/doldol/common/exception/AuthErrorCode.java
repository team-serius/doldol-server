package doldol_server.doldol.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    // 401
    WRONG_ID_PW(HttpStatus.UNAUTHORIZED, "A-001", "아이디 혹은 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A-002", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A-003", "만료된 토큰입니다."),

    // 403
    INCORRECT_CLAIM_TOKEN(HttpStatus.FORBIDDEN, "A-004", "잘못된 토큰입니다."),
    USER_NOT_FOUND(HttpStatus.FORBIDDEN, "A-005", "회원을 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A-006", "접근이 거부되었습니다.");

    private HttpStatus httpStatus;
    private String code;
    private String message;
}