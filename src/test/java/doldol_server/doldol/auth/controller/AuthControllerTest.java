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

import doldol_server.doldol.auth.dto.request.EmailCodeSendRequest;
import doldol_server.doldol.auth.dto.request.EmailCodeVerifyRequest;
import doldol_server.doldol.auth.dto.request.FinalJoinRequest;
import doldol_server.doldol.auth.dto.request.TempJoinRequest;
import doldol_server.doldol.auth.service.AuthService;
import doldol_server.doldol.common.ControllerTest;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest extends ControllerTest {

	@MockitoBean
	private AuthService authService;

	@Test
	@DisplayName("임시 회원가입 - 아이디의 길이가 3 이하이면 오류를 발생시킵니다.")
	void tempJoin_ValidationFail_IdTooShort() throws Exception {
		// given
		TempJoinRequest request = new TempJoinRequest(
			"tes",
			"Password123!",
			"Password123!",
			"김돌돌",
			"01012341234",
			"test@example.com",
			true,
			true,
			true
		);

		// when & then
		mockMvc.perform(post("/auth/temp-join")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).tempJoin(any(TempJoinRequest.class));
	}

	@Test
	@DisplayName("임시 회원가입 - 아이디의 길이가 21 이상이면 오류를 발생시킵니다.")
	void tempJoin_ValidationFail_IdTooLong() throws Exception {
		// given
		TempJoinRequest request = new TempJoinRequest(
			"a".repeat(21),
			"Password123!",
			"Password123!",
			"김돌돌",
			"01012341234",
			"test@example.com",
			true,
			true,
			true
		);

		// when & then
		mockMvc.perform(post("/auth/temp-join")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).tempJoin(any(TempJoinRequest.class));
	}

	@Test
	@DisplayName("임시 회원가입 - 비밀번호 패턴 불일치면 오류를 발생시킵니다.")
	void tempJoin_ValidationFail_InvalidPassword() throws Exception {
		// given
		TempJoinRequest request = new TempJoinRequest(
			"testuser123",
			"weakpass",
			"weakpass",
			"김돌돌",
			"01012341234",
			"test@example.com",
			true,
			true,
			true
		);

		// when & then
		mockMvc.perform(post("/auth/temp-join")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).tempJoin(any(TempJoinRequest.class));
	}

	@Test
	@DisplayName("임시 회원가입 - 잘못된 이메일 형식이면 오류를 발생시킵니다.")
	void tempJoin_ValidationFail_InvalidEmail() throws Exception {
		// given
		TempJoinRequest request = new TempJoinRequest(
			"testuser123",
			"Password123!",
			"Password123!",
			"김돌돌",
			"01012341234",
			"email.com",
			true,
			true,
			true
		);

		// when & then
		mockMvc.perform(post("/auth/temp-join")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).tempJoin(any(TempJoinRequest.class));
	}

	@Test
	@DisplayName("임시 회원가입 - 잘못된 전화번호 형식이면 오류를 발생시킵니다.")
	void tempJoin_ValidationFail_InvalidPhoneNumber() throws Exception {
		// given
		TempJoinRequest request = new TempJoinRequest(
			"testuser123",
			"Password123!",
			"Password123!",
			"김돌돌",
			"0201234567",
			"test@example.com",
			true,
			true,
			true
		);

		// when & then
		mockMvc.perform(post("/auth/temp-join")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).tempJoin(any(TempJoinRequest.class));
	}

	@Test
	@DisplayName("임시 회원가입 - 이름이 너무 길면 오류를 발생시킵니다.")
	void tempJoin_ValidationFail_NameTooLong() throws Exception {
		// given
		TempJoinRequest request = new TempJoinRequest(
			"testuser123",
			"Password123!",
			"Password123!",
			"김돌돌돌돌돌",
			"01012341234",
			"test@example.com",
			true,
			true,
			true
		);

		// when & then
		mockMvc.perform(post("/auth/temp-join")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).tempJoin(any(TempJoinRequest.class));
	}

	@Test
	@DisplayName("임시 회원가입 - 이름이 한글이 아니면 오류를 발생시킵니다.")
	void tempJoin_ValidationFail_NameNotKorean() throws Exception {
		// given
		TempJoinRequest request = new TempJoinRequest(
			"testuser123",
			"Password123!",
			"Password123!",
			"kimdoldol",
			"01012341234",
			"test@example.com",
			true,
			true,
			true
		);

		// when & then
		mockMvc.perform(post("/auth/temp-join")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).tempJoin(any(TempJoinRequest.class));
	}

	@Test
	@DisplayName("임시 회원가입 - 약관 동의하지 않으면 오류를 발생시킵니다.")
	void tempJoin_ValidationFail_TermsNotAgreed() throws Exception {
		// given
		TempJoinRequest request = new TempJoinRequest(
			"testuser123",
			"Password123!",
			"Password123!",
			"김돌돌",
			"01012341234",
			"test@example.com",
			false,
			true,
			true
		);

		// when & then
		mockMvc.perform(post("/auth/temp-join")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isBadRequest());

		verify(authService, never()).tempJoin(any(TempJoinRequest.class));
	}

	@Test
	@DisplayName("이메일 인증 코드 전송 - 성공")
	void sendVerificationCode_Success() throws Exception {
		// given
		EmailCodeSendRequest request = new EmailCodeSendRequest("test@example.com");
		doNothing().when(authService).sendVerificationCode(anyString());

		// when & then
		mockMvc.perform(post("/auth/email/send-code")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isNoContent());

		verify(authService).sendVerificationCode("test@example.com");
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
	@DisplayName("회원가입 완료 - 성공")
	void join_Success() throws Exception {
		// given
		FinalJoinRequest request = new FinalJoinRequest("test@example.com");
		doNothing().when(authService).join(anyString());

		// when & then
		mockMvc.perform(post("/auth/join")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isNoContent());

		verify(authService).join("test@example.com");
	}

	@Test
	@DisplayName("필수 필드 누락 - 400 Bad Request")
	void tempJoin_ValidationFail_MissingRequiredFields() throws Exception {
		// given
		String emptyJson = "{}";

		// when & then
		mockMvc.perform(post("/auth/temp-join")
				.contentType(MediaType.APPLICATION_JSON)
				.content(emptyJson))
			.andExpect(status().isBadRequest());

		verify(authService, never()).tempJoin(any(TempJoinRequest.class));
	}

}