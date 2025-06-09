package doldol_server.doldol.common.exception;

import doldol_server.doldol.common.exception.errorCode.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
	private final ErrorCode errorCode;
	private final Object[] args;

	protected CustomException(ErrorCode errorCode, String message) {
		super(errorCode.getMessage() + ": " + message);
		this.errorCode = errorCode;
		this.args = null;
	}

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.args = null;
	}

	public CustomException(ErrorCode errorCode, Object... args) {
		super(String.format(errorCode.getMessage(), args));
		this.errorCode = errorCode;
		this.args = args;
	}
}
