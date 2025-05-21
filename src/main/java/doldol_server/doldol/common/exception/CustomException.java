package doldol_server.doldol.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
	private final ErrorCode errorCode;

	protected CustomException(ErrorCode errorCode, String message) {
		super(errorCode.getMessage() + ": " + message);
		this.errorCode = errorCode;
	}

	protected CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
