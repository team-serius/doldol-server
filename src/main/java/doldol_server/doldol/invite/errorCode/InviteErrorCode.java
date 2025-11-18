package doldol_server.doldol.invite.errorCode;

import doldol_server.doldol.common.exception.errorCode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum InviteErrorCode implements ErrorCode {

    INVITE_NOT_FOUND(HttpStatus.NOT_FOUND, "IV-001", "초대장을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

