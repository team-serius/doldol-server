package doldol_server.doldol.common.exception;

import doldol_server.doldol.common.exception.errorCode.AuthErrorCode;
import doldol_server.doldol.common.exception.errorCode.CommonErrorCode;
import doldol_server.doldol.common.exception.errorCode.ErrorCode;
import doldol_server.doldol.common.response.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 커스텀 예외
	 */
	@ExceptionHandler(value = CustomException.class)
	public ResponseEntity<ErrorResponse<Void>> handleCustomException(CustomException e) {
		ErrorCode errorCode = e.getErrorCode();
		log.warn("커스텀 예외 발생: 코드={}, 메시지={}", errorCode.getCode(), e.getMessage());

		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), e.getMessage()));
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

		log.warn("유효성 검사 실패: 필드 오류={}", errors);

		CommonErrorCode errorCode = CommonErrorCode.INVALID_VALUE;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errors, errorCode.getCode(), errorCode.getMessage()));
	}

	/**
	 * 인증 예외
	 */
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse<Void>> handleAuthenticationException(AuthenticationException e) {
		log.warn("인증 예외 발생: {}", e.getMessage());

		AuthErrorCode errorCode = AuthErrorCode.INVALID_TOKEN;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), errorCode.getMessage()));
	}

	/**
	 * 인가 예외
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
		log.warn("접근 권한 없음: {}", e.getMessage());

		AuthErrorCode errorCode = AuthErrorCode.ACCESS_DENIED;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), errorCode.getMessage()));
	}

	/**
	 * IllegalArgumentException 처리
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
		log.warn("잘못된 인자: {}", e.getMessage());

		CommonErrorCode errorCode = CommonErrorCode.INVALID_ARGUMENT;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), errorCode.getMessage()));
	}

	/**
	 * RuntimeException 처리
	 */
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse<Void>> handleRuntimeException(RuntimeException e) {
		log.error("런타임 예외 발생: {}", e.getMessage(), e);

		CommonErrorCode errorCode = CommonErrorCode.RUNTIME_ERROR;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), errorCode.getMessage()));
	}

	/**
	 * 일반적인 예외 처리 (catch-all)
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse<Void>> handleGeneralException(Exception e) {
		log.error("예상치 못한 예외 발생: {}", e.getMessage(), e);

		CommonErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), errorCode.getMessage()));
	}

	/**
	 * 필수 요청 파라미터 누락 예외
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse<Void>> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException e) {
		log.warn("필수 파라미터 누락: 파라미터={}, 타입={}", e.getParameterName(), e.getParameterType());

		CommonErrorCode errorCode = CommonErrorCode.MISSING_PARAMETER;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), errorCode.getMessage()));
	}

	/**
	 * OAuth2 연동 해제 예외
	 */
	@ExceptionHandler(OAuth2UnlinkException.class)
	public ResponseEntity<ErrorResponse<Void>> handleOAuth2UnlinkException(OAuth2UnlinkException e) {
		log.error("OAuth2 연동 해제 실패: 코드={}, 메시지={}", e.getErrorCode().getCode(), e.getMessage(), e);

		return ResponseEntity.status(e.getErrorCode().getHttpStatus())
			.body(ErrorResponse.error(e.getErrorCode().getCode(), e.getMessage()));
	}
}