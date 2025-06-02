package doldol_server.doldol.common.exception.errorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    // 400
    VERIFICATION_CODE_WRONG(HttpStatus.BAD_REQUEST, "A-009", "인증번호가 틀렸습니다."),

    // 401
    WRONG_ID_PW(HttpStatus.UNAUTHORIZED, "A-001", "아이디 혹은 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A-002", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A-003", "만료된 토큰입니다."),
    UNVERIFIED_EMAIL(HttpStatus.UNAUTHORIZED, "A-010", "이메일 인증을 하셔야합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A-014", "토큰이 만료되었습니다."),

    // 403
    INCORRECT_CLAIM_TOKEN(HttpStatus.FORBIDDEN, "A-004", "잘못된 토큰입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A-005", "접근이 거부되었습니다."),

    // 404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "A-006", "회원을 찾을 수 없습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "A-008", "입력하신 이메일을 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "A-013", "리프레시 토큰을 찾을 수 없습니다."),

    // 409
    ID_DUPLICATED(HttpStatus.CONFLICT,"A-007","이미 사용중인 아이디입니다."),
    EMAIl_DUPLICATED(HttpStatus.CONFLICT,"A-011","이미 사용중인 이메일입니다."),
    PHONE_DUPLICATED(HttpStatus.CONFLICT,"A-012","이미 사용중인 전화번호입니다."),
    ;

    private HttpStatus httpStatus;
    private String code;
    private String message;
}
