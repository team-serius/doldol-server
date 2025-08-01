package doldol_server.doldol.auth.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import doldol_server.doldol.common.exception.errorCode.ErrorCode;
import doldol_server.doldol.common.response.ApiResponse;
import doldol_server.doldol.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseUtil {

	/**
	 * No-Content 반환
	 */
	public static <T> void writeNoContent(HttpServletResponse response,
		ObjectMapper objectMapper,
		HttpStatus status) throws IOException {

		setJsonResponse(response, status.value());
		response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.noContent()));
	}

	/**
	 * 성공 응답을 JSON 형태로 반환 (여러 헤더 포함)
	 */
	public static <T> void writeSuccessResponseWithHeaders(HttpServletResponse response,
		ObjectMapper objectMapper,
		T data,
		HttpStatus status) throws IOException {

		ApiResponse<T> apiResponse = createApiResponse(data, status);
		setJsonResponse(response, status.value());
		response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
	}

	/**
	 * 에러 응답을 JSON 형태로 반환
	 */
	public static void writeErrorResponse(HttpServletResponse response,
		ObjectMapper objectMapper,
		ErrorCode errorCode) throws IOException {
		ErrorResponse<Void> errorResponse = ErrorResponse.error(
			errorCode.getCode(),
			errorCode.getMessage()
		);

		setJsonResponse(response, errorCode.getHttpStatus().value());
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}

	/**
	 * 응답 헤더 기본 설정 (JSON 형태)
	 */
	private static void setJsonResponse(HttpServletResponse response, int status) {
		response.setStatus(status);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
	}

	/**
	 * ApiResponse 생성 헬퍼 메서드
	 */
	private static <T> ApiResponse<T> createApiResponse(T data, HttpStatus status) {
		return (ApiResponse<T>)switch (status) {
			case CREATED -> ApiResponse.created(data);
			case NO_CONTENT -> ApiResponse.noContent();
			default -> ApiResponse.ok(data);
		};
	}
}
