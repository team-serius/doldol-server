package doldol_server.doldol.common.exception;

import doldol_server.doldol.common.response.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}

		CommonErrorCode errorCode = CommonErrorCode.INVALID_VALUE;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errors, errorCode.getCode(), errorCode.getMessage()));
	}

	/**
	 * 인증 예외
	 */
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse<Void>> handleAuthenticationException(AuthenticationException e) {
		AuthErrorCode errorCode = AuthErrorCode.INVALID_TOKEN;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), errorCode.getMessage()));
	}

	/**
	 * 인가 예외
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
		AuthErrorCode errorCode = AuthErrorCode.ACCESS_DENIED;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), errorCode.getMessage()));
	}

	/**
	 * IllegalArgumentException 처리
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
		CommonErrorCode errorCode = CommonErrorCode.INVALID_ARGUMENT;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), errorCode.getMessage()));
	}

	/**
	 * RuntimeException 처리
	 */
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse<Void>> handleRuntimeException(RuntimeException e) {
		CommonErrorCode errorCode = CommonErrorCode.RUNTIME_ERROR;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), errorCode.getMessage()));
	}

	/**
	 * 일반적인 예외 처리 (catch-all)
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse<Void>> handleGeneralException(Exception e) {
		CommonErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), errorCode.getMessage()));
	}
}