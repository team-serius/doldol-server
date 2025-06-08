package doldol_server.doldol.common.exception.errorCode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2ErrorCode implements ErrorCode {
    INVALID_SOCIAL_ID(HttpStatus.BAD_REQUEST, "OA-001", "유효하지 않은 소셜 ID입니다."),
    OAUTH2_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "OA-002", "OAuth2 인증에 실패했습니다."),
    OAUTH2_BAD_REQUEST(HttpStatus.BAD_REQUEST, "OA-003", "OAuth2 요청이 잘못되었습니다."),
    OAUTH2_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OA-004", "OAuth2 API 호출에 실패했습니다."),
    OAUTH2_UNLINK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OA-005", "OAuth2 연결 해제에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}