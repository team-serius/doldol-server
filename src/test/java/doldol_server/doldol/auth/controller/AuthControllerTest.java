package doldol_server.doldol.auth.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import doldol_server.doldol.auth.dto.request.EmailCheckRequest;
import doldol_server.doldol.auth.dto.request.EmailCodeVerifyRequest;
import doldol_server.doldol.auth.dto.request.IdCheckRequest;
import doldol_server.doldol.auth.dto.request.OAuthRegisterRequest;
import doldol_server.doldol.auth.dto.request.PhoneCheckRequest;
import doldol_server.doldol.auth.dto.request.RegisterRequest;
import doldol_server.doldol.auth.service.AuthService;
import doldol_server.doldol.common.ControllerTest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest extends ControllerTest {

	@MockitoBean
	private AuthService authService;

	@Test
	@DisplayName("아이디 중복 확인 - 성공")
	void checkIdDuplicate_Success() throws Exception {
		// given
		IdCheckRequest request = new IdCheckRequest("test");
		doNothing().when(authService).checkIdDuplicate(anyString());

		// when & then
		mockMvc.perform(post("/auth/check-id")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isNoContent());

		verify(authService).checkIdDuplicate("test");
	}

	@Test
	@DisplayName("이메일 중복 확인 - 성공")
	void checkEmailDuplicate_Success() throws Exception {
		// given
		EmailCheckRequest request = new EmailCheckRequest("test@example.com");
		doNothing().when(authService).checkEmailDuplicate(anyString());

		// when & then
		mockMvc.perform(post("/auth/check-email")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isNoContent());

		verify(authService).checkEmailDuplicate("test@example.com");
	}

	@Test
	@DisplayName("전화번호 중복 확인 - 성공")
	void checkPhoneDuplicate_Success() throws Exception {
		// given
		PhoneCheckRequest request = new PhoneCheckRequest("01001010101");
		doNothing().when(authService).checkPhoneDuplicate(anyString());

		// when & then
		mockMvc.perform(post("/auth/check-phone")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isNoContent());

		verify(authService).checkPhoneDuplicate("01001010101");
	}

	@Test
	@DisplayName("아이디 길이가 3 이하이면 오류를 발생시킵니다.")
	void checkIdDuplicate_ValidationFail_IdTooShort() throws Exception {
		// given
		IdCheckRequest request = new IdCheckRequest("tes");

		// when & then
		mockMvc.perform(post("/auth/check-id")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).checkIdDuplicate(any());
	}

	@Test
	@DisplayName("아이디 길이가 21 이상이면 오류를 발생시킵니다.")
	void checkIdDuplicate_ValidationFail_IdTooLong() throws Exception {
		// given
		IdCheckRequest request = new IdCheckRequest("abcdefghijklm2132321442");

		// when & then
		mockMvc.perform(post("/auth/check-id")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).checkIdDuplicate(any());
	}

	@Test
	@DisplayName("이메일 인증 코드 검증 - 성공")
	void validateVerificationCode_Success() throws Exception {
		// given
		EmailCodeVerifyRequest request = new EmailCodeVerifyRequest("test@example.com", "123456");
		doNothing().when(authService).validateVerificationCode(anyString(), anyString());

		// when & then
		mockMvc.perform(post("/auth/email/verify-code")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isNoContent());

		verify(authService).validateVerificationCode("test@example.com", "123456");
	}

	@Test
	@DisplayName("자체 회원가입 - 성공")
	void register_Success() throws Exception {
		// given
		RegisterRequest request = new RegisterRequest(
			"testuser123",
			"Password123!",
			"김돌돌",
			"01012341234",
			"test@example.com"
		);
		doNothing().when(authService).register(any(RegisterRequest.class));

		// when & then
		mockMvc.perform(post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isNoContent());

		verify(authService).register(any(RegisterRequest.class));
	}

	@Test
	@DisplayName("소셜 회원가입 - 성공")
	void oauthRegister_Success() throws Exception {
		// given
		OAuthRegisterRequest request = new OAuthRegisterRequest(
			"김돌돌",
			"01012341234",
			"test@example.com",
			"kakao123456",
			"KAKAO"
		);
		doNothing().when(authService).oauthRegister(any(OAuthRegisterRequest.class));

		// when & then
		mockMvc.perform(post("/auth/oauth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isNoContent());

		verify(authService).oauthRegister(any(OAuthRegisterRequest.class));
	}

	@Test
	@DisplayName("자체 회원가입 - 아이디의 길이가 3 이하이면 오류를 발생시킵니다.")
	void register_ValidationFail_IdTooShort() throws Exception {
		// given
		RegisterRequest request = new RegisterRequest(
			"tes",
			"Password123!",
			"김돌돌",
			"01012341234",
			"test@example.com"
		);

		// when & then
		mockMvc.perform(post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).register(any(RegisterRequest.class));
	}

	@Test
	@DisplayName("자체 회원가입 - 아이디의 길이가 21 이상이면 오류를 발생시킵니다.")
	void register_ValidationFail_IdTooLong() throws Exception {
		// given
		RegisterRequest request = new RegisterRequest(
			"a".repeat(21),
			"Password123!",
			"김돌돌",
			"01012341234",
			"test@example.com"
		);

		// when & then
		mockMvc.perform(post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).register(any(RegisterRequest.class));
	}

	@Test
	@DisplayName("자체 회원가입 - 비밀번호 패턴 불일치면 오류를 발생시킵니다.")
	void register_ValidationFail_InvalidPassword() throws Exception {
		// given
		RegisterRequest request = new RegisterRequest(
			"testuser123",
			"weakpass",
			"김돌돌",
			"01012341234",
			"test@example.com"
		);

		// when & then
		mockMvc.perform(post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).register(any(RegisterRequest.class));
	}

	@Test
	@DisplayName("자체 회원가입 - 잘못된 이메일 형식이면 오류를 발생시킵니다.")
	void register_ValidationFail_InvalidEmail() throws Exception {
		// given
		RegisterRequest request = new RegisterRequest(
			"testuser123",
			"Password123!",
			"김돌돌",
			"01012341234",
			"email.com"
		);

		// when & then
		mockMvc.perform(post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).register(any(RegisterRequest.class));
	}

	@Test
	@DisplayName("자체 회원가입 - 잘못된 전화번호 형식이면 오류를 발생시킵니다.")
	void register_ValidationFail_InvalidPhone() throws Exception {
		// given
		RegisterRequest request = new RegisterRequest(
			"testuser123",
			"Password123!",
			"김돌돌",
			"0201234567",
			"test@example.com"
		);

		// when & then
		mockMvc.perform(post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).register(any(RegisterRequest.class));
	}

	@Test
	@DisplayName("자체 회원가입 - 이름이 너무 길면 오류를 발생시킵니다.")
	void register_ValidationFail_NameTooLong() throws Exception {
		// given
		RegisterRequest request = new RegisterRequest(
			"testuser123",
			"Password123!",
			"김돌돌돌돌돌",
			"01012341234",
			"test@example.com"
		);

		// when & then
		mockMvc.perform(post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).register(any(RegisterRequest.class));
	}

	@Test
	@DisplayName("자체 회원가입 - 이름이 한글이 아니면 오류를 발생시킵니다.")
	void register_ValidationFail_NameNotKorean() throws Exception {
		// given
		RegisterRequest request = new RegisterRequest(
			"testuser123",
			"Password123!",
			"kimdoldol",
			"01012341234",
			"test@example.com"
		);

		// when & then
		mockMvc.perform(post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).register(any(RegisterRequest.class));
	}

	@Test
	@DisplayName("소셜 회원가입 - 이름이 비어있으면 오류를 발생시킵니다.")
	void oauthRegister_ValidationFail_NameBlank() throws Exception {
		// given
		OAuthRegisterRequest request = new OAuthRegisterRequest(
			"",
			"01012341234",
			"test@example.com",
			"kakao123456",
			"KAKAO"
		);

		// when & then
		mockMvc.perform(post("/auth/oauth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).oauthRegister(any(OAuthRegisterRequest.class));
	}

	@Test
	@DisplayName("소셜 회원가입 - 소셜 아이디가 비어있으면 오류를 발생시킵니다.")
	void oauthRegister_ValidationFail_SocialIdBlank() throws Exception {
		// given
		OAuthRegisterRequest request = new OAuthRegisterRequest(
			"김돌돌",
			"01012341234",
			"test@example.com",
			"",
			"KAKAO"
		);

		// when & then
		mockMvc.perform(post("/auth/oauth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).oauthRegister(any(OAuthRegisterRequest.class));
	}

	@Test
	@DisplayName("토큰 재발급 - 성공")
	void reissue_Success() throws Exception {
		// given
		String refreshToken = "Bearer_valid_refresh_token";
		doNothing().when(authService).reissue(anyString(), any(HttpServletResponse.class));

		// when & then
		mockMvc.perform(post("/auth/reissue")
				.cookie(new Cookie("Refresh-Token", refreshToken)))
			.andExpect(status().isNoContent());

		verify(authService).reissue(eq(refreshToken), any(HttpServletResponse.class));
	}

	@Test
	@DisplayName("토큰 재발급 - 리프레시 토큰 쿠키가 없으면 서버 오류를 발생시킵니다")
	void reissue_Fail_MissingRefreshTokenCookie() throws Exception {
		// when & then
		mockMvc.perform(post("/auth/reissue"))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").value("C-003"))
			.andExpect(jsonPath("$.message").value("서버 내부 오류가 발생했습니다."));

		verify(authService, never()).reissue(anyString(), any(HttpServletResponse.class));
	}
}