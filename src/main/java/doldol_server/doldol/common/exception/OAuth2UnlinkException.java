package doldol_server.doldol.common.exception;

import doldol_server.doldol.common.exception.errorCode.ErrorCode;
import lombok.Getter;

@Getter
public class OAuth2UnlinkException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public OAuth2UnlinkException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public OAuth2UnlinkException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}