package doldol_server.doldol.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

	HttpStatus getHttpStatus();
	String getCode();
	String getMessage();
}
