package doldol_server.doldol.common.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import lombok.Getter;

@Getter
public class CustomOAuth2Exception extends OAuth2AuthenticationException {
    
    private final ErrorCode errorCode;

    public CustomOAuth2Exception(ErrorCode errorCode) {
        super(new OAuth2Error(errorCode.getCode(), errorCode.getMessage(), null));
        this.errorCode = errorCode;
    }

    public CustomOAuth2Exception(ErrorCode errorCode, String description) {
        super(new OAuth2Error(errorCode.getCode(), errorCode.getMessage(), description));
        this.errorCode = errorCode;
    }
}