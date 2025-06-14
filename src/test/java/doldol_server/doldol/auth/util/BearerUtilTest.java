package doldol_server.doldol.auth.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

class BearerUtilTest {

	@Test
	@DisplayName("유효한 Bearer 토큰 추출 - 성공")
	void extractBearerToken_Success() {
		// given
		HttpServletRequest request = mock(HttpServletRequest.class);
		String authHeader = "Bearer test.test.token";
		when(request.getHeader("Authorization")).thenReturn(authHeader);

		// when
		Optional<String> result = BearerUtil.extractBearerToken(request);

		// then
		assertTrue(result.isPresent());
		assertEquals("test.test.token", result.get());
	}

	@Test
	@DisplayName("Bearer 토큰에 공백이 있어도 정상 추출 - 성공")
	void extractBearerToken_WithSpaces_Success() {
		// given
		HttpServletRequest request = mock(HttpServletRequest.class);
		String authHeader = "Bearer   test.test.token   ";
		when(request.getHeader("Authorization")).thenReturn(authHeader);

		// when
		Optional<String> result = BearerUtil.extractBearerToken(request);

		// then
		assertTrue(result.isPresent());
		assertEquals("test.test.token", result.get());
	}

	@Test
	@DisplayName("Authorization 헤더가 null인 경우 빈 Optional 반환")
	void extractBearerToken_NullHeader_ReturnsEmpty() {
		// given
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("Authorization")).thenReturn(null);

		// when
		Optional<String> result = BearerUtil.extractBearerToken(request);

		// then
		assertFalse(result.isPresent());
	}

	@Test
	@DisplayName("Authorization 헤더가 빈 문자열인 경우 빈 Optional 반환")
	void extractBearerToken_EmptyHeader_ReturnsEmpty() {
		// given
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("Authorization")).thenReturn("");

		// when
		Optional<String> result = BearerUtil.extractBearerToken(request);

		// then
		assertFalse(result.isPresent());
	}

	@Test
	@DisplayName("Authorization 헤더가 공백만 있는 경우 빈 Optional 반환")
	void extractBearerToken_WhitespaceHeader_ReturnsEmpty() {
		// given
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("Authorization")).thenReturn("   ");

		// when
		Optional<String> result = BearerUtil.extractBearerToken(request);

		// then
		assertFalse(result.isPresent());
	}

	@Test
	@DisplayName("Bearer 접두사가 없는 경우 빈 Optional 반환")
	void extractBearerToken_NoBearerPrefix_ReturnsEmpty() {
		// given
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0");

		// when
		Optional<String> result = BearerUtil.extractBearerToken(request);

		// then
		assertFalse(result.isPresent());
	}

	@Test
	@DisplayName("Bearer만 있고 토큰이 없는 경우 빈 Optional 반환")
	void extractBearerToken_BearerOnly_ReturnsEmpty() {
		// given
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("Authorization")).thenReturn("Bearer");

		// when
		Optional<String> result = BearerUtil.extractBearerToken(request);

		// then
		assertFalse(result.isPresent());
	}

	@Test
	@DisplayName("Bearer 뒤에 공백만 있는 경우 빈 Optional 반환")
	void extractBearerToken_BearerWithSpacesOnly_ReturnsEmpty() {
		// given
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("Authorization")).thenReturn("Bearer   ");

		// when
		Optional<String> result = BearerUtil.extractBearerToken(request);

		// then
		assertFalse(result.isPresent());
	}
}