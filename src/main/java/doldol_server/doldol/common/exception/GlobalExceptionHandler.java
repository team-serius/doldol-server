package doldol_server.doldol.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import doldol_server.doldol.common.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 커스텀 예외
	 */
	@ExceptionHandler(value = CustomException.class)
	public ResponseEntity<ErrorResponse<Void>> handleCustomException(CustomException e) {
		ErrorCode errorCode = e.getErrorCode();
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), errorCode.getMessage()));
	}

	/**
	 * 데이터 유효성 검사가 실패할 경우
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse<Map<String, String>>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();

		BindingResult bindingResult = e.getBindingResult();
		for (FieldError er : bindingResult.getFieldErrors()) {
			errors.put(er.getField(), er.getDefaultMessage());
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ErrorResponse.error(errors, ErrorCode.INVALID_VALUE.getCode(),
				ErrorCode.INVALID_VALUE.getMessage()));
	}
}
