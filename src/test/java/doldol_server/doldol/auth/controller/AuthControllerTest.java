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
import doldol_server.doldol.auth.dto.request.ReissueTokenRequest;
import doldol_server.doldol.auth.dto.request.UserInfoIdCheckRequest;
import doldol_server.doldol.auth.dto.response.ReissueTokenResponse;
import doldol_server.doldol.auth.dto.response.UserLoginIdResponse;
import doldol_server.doldol.auth.service.AuthService;
import doldol_server.doldol.common.ControllerTest;
import doldol_server.doldol.user.entity.SocialType;

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
			.andExpect(status().isOk());

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
			.andExpect(status().isOk());

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
			.andExpect(status().isOk());

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
			.andExpect(status().isOk());

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
			.andExpect(status().isOk());

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
			SocialType.KAKAO
		);
		doNothing().when(authService).oauthRegister(any(OAuthRegisterRequest.class));

		// when & then
		mockMvc.perform(post("/auth/oauth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isOk());

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
			SocialType.KAKAO
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
			SocialType.KAKAO
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
		ReissueTokenRequest request = new ReissueTokenRequest("valid_refresh_token");
		ReissueTokenResponse response = ReissueTokenResponse.builder()
			.accessToken("new_access_token")
			.refreshToken("new_refresh_token")
			.build();

		when(authService.reissue(anyString())).thenReturn(response);

		// when & then
		mockMvc.perform(post("/auth/reissue")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(200))
			.andExpect(jsonPath("$.data.accessToken").value("new_access_token"))
			.andExpect(jsonPath("$.data.refreshToken").value("new_refresh_token"));

		verify(authService).reissue("valid_refresh_token");
	}


	@Test
	@DisplayName("회원 탈퇴 - 성공")
	void withdraw_Success() throws Exception {
		// given
		Long userId = 1L;
		doNothing().when(authService).withdraw(anyLong());

		// when & then
		mockMvc.perform(post("/auth/withdraw")
				.queryParam("userId", String.valueOf(1L))
				.with(mockUser(userId))) // ControllerTest의 mockUser 메서드 사용
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(204));

		verify(authService).withdraw(eq(userId));
	}

	@Test
	@DisplayName("사용자 정보 검증 - 성공")
	void validateUserInfo_Success() throws Exception {
		// given
		UserInfoIdCheckRequest request = new UserInfoIdCheckRequest(
			"test@example.com",
			"01012341234"
		);
		doNothing().when(authService).validateUserInfo(any(UserInfoIdCheckRequest.class));

		// when & then
		mockMvc.perform(post("/auth/validate/user/info")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(204));

		verify(authService).validateUserInfo(any(UserInfoIdCheckRequest.class));
	}

	@Test
	@DisplayName("아이디 찾기 - 성공")
	void getLoginId_Success() throws Exception {
		// given
		String email = "test@example.com";
		UserLoginIdResponse response = new UserLoginIdResponse("testuser123");

		when(authService.getLoginId(email)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/auth/find/id")
				.param("email", email))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(200))
			.andExpect(jsonPath("$.data.id").value("testuser123"));

		verify(authService).getLoginId(email);
	}

	@Test
	@DisplayName("비밀번호 초기화 - 성공")
	void resetPassword_Success() throws Exception {
		// given
		String email = "test@example.com";
		doNothing().when(authService).resetPassword(email);

		// when & then
		mockMvc.perform(patch("/auth/reset/password")
				.param("email", email))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(204));

		verify(authService).resetPassword(email);
	}
}