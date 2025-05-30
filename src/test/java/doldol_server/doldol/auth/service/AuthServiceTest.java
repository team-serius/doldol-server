package doldol_server.doldol.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import doldol_server.doldol.auth.dto.request.OAuthRegisterRequest;
import doldol_server.doldol.auth.dto.request.RegisterRequest;
import doldol_server.doldol.common.ServiceTest;
import doldol_server.doldol.common.exception.AuthErrorCode;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.user.repository.UserRepository;

@DisplayName("Auth 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest extends ServiceTest {

	@Autowired
	private AuthService authService;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private EmailService emailService;

	@MockitoBean
	private RedisTemplate<String, Object> redisTemplate;

	@MockitoBean
	private ValueOperations<String, Object> valueOperations;

	@MockitoBean
	private PasswordEncoder passwordEncoder;

	private static final String EMAIL_VERIFIED_KEY = "verified";

	@Test
	@DisplayName("아이디가 중복이면 예외를 발생시킨다")
	void checkIdDuplicate_ThrowsException_WhenDuplicated() {
		// given
		String loginId = "duplicateduser";
		when(userRepository.existsByLoginId(loginId)).thenReturn(true);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.checkIdDuplicate(loginId));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.ID_DUPLICATED);
	}

	@Test
	@DisplayName("이메일이 중복이면 예외를 발생시킨다")
	void checkEmailDuplicate_ThrowsException_WhenDuplicated() {
		// given
		String email = "test@example.com";
		when(userRepository.existsByEmail(email)).thenReturn(true);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.checkEmailDuplicate(email));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.EMAIl_DUPLICATED);
	}

	@Test
	@DisplayName("전화번호가 중복이면 예외를 발생시킨다")
	void checkPhoneDuplicate_ThrowsException_WhenDuplicated() {
		// given
		String phone = "01012341234";
		when(userRepository.existsByPhone(phone)).thenReturn(true);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.checkPhoneDuplicate(phone));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.PHONE_DUPLICATED);
	}

	@Test
	@DisplayName("인증 코드 전송이 성공적으로 처리된다")
	void sendVerificationCode_Success() {
		// given
		String email = "test@example.com";

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		doNothing().when(valueOperations).set(eq(email), anyString(), anyLong(), any());
		doNothing().when(emailService).sendEmailVerificationCode(eq(email), anyString());

		// when & then
		assertDoesNotThrow(() -> authService.sendVerificationCode(email));

		verify(emailService).sendEmailVerificationCode(eq(email), anyString());
		verify(valueOperations).set(eq(email), anyString(), anyLong(), any());
	}

	@Test
	@DisplayName("인증 코드 검증이 성공적으로 처리된다")
	void validateVerificationCode_Success() {
		// given
		String email = "test@example.com";
		String verificationCode = "123456";

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(email)).thenReturn(verificationCode);
		doNothing().when(valueOperations).set(eq(email), eq(EMAIL_VERIFIED_KEY), anyLong(), any());

		// when & then
		assertDoesNotThrow(() -> authService.validateVerificationCode(email, verificationCode));

		verify(valueOperations).set(eq(email), eq(EMAIL_VERIFIED_KEY), anyLong(), any());
	}

	@Test
	@DisplayName("인증 코드가 존재하지 않으면 예외를 발생시킨다")
	void validateVerificationCode_ThrowsException_WhenCodeNotFound() {
		// given
		String email = "test@example.com";
		String verificationCode = "123456";

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(email)).thenReturn(null);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.validateVerificationCode(email, verificationCode));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.EMAIL_NOT_FOUND);
	}

	@Test
	@DisplayName("잘못된 인증 코드로 검증 시 예외를 발생시킨다")
	void validateVerificationCode_ThrowsException_WhenCodeWrong() {
		// given
		String email = "test@example.com";
		String correctCode = "123456";
		String wrongCode = "654321";

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(email)).thenReturn(correctCode);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.validateVerificationCode(email, wrongCode));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.VERIFICATION_CODE_WRONG);
	}

	@Test
	@DisplayName("자체 회원가입이 성공적으로 처리된다")
	void register_Success() {
		// given
		RegisterRequest request = new RegisterRequest(
			"testuser",
			"Password123!",
			"김돌돌",
			"01012341234",
			"test@example.com"
		);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(request.email())).thenReturn(EMAIL_VERIFIED_KEY);
		when(redisTemplate.delete(request.email())).thenReturn(true);
		when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
		when(userRepository.save(any())).thenReturn(null);

		// when & then
		assertDoesNotThrow(() -> authService.register(request));

		verify(userRepository).save(any());
		verify(redisTemplate).delete(request.email());
	}

	@Test
	@DisplayName("이메일 인증이 되지 않은 상태로 자체 회원가입 시 예외를 발생시킨다")
	void register_ThrowsException_WhenEmailNotVerified() {
		// given
		RegisterRequest request = new RegisterRequest(
			"testuser",
			"Password123!",
			"김돌돌",
			"01012341234",
			"test@example.com"
		);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(request.email())).thenReturn(null);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.register(request));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.UNVERIFIED_EMAIL);
	}

	@Test
	@DisplayName("이메일 인증 값이 올바르지 않으면 자체 회원가입 시 예외를 발생시킨다")
	void register_ThrowsException_WhenEmailVerificationWrong() {
		// given
		RegisterRequest request = new RegisterRequest(
			"testuser",
			"Password123!",
			"김돌돌",
			"01012341234",
			"test@example.com"
		);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(request.email())).thenReturn("wrong_verification");

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.register(request));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.UNVERIFIED_EMAIL);
	}

	@Test
	@DisplayName("소셜 회원가입이 성공적으로 처리된다")
	void oauthRegister_Success() {
		// given
		OAuthRegisterRequest request = new OAuthRegisterRequest(
			"김돌돌",
			"01012341234",
			"test@example.com",
			"kakao123456",
			"KAKAO"
		);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(request.email())).thenReturn(EMAIL_VERIFIED_KEY);
		when(redisTemplate.delete(request.email())).thenReturn(true);
		when(userRepository.save(any())).thenReturn(null);

		// when & then
		assertDoesNotThrow(() -> authService.oauthRegister(request));

		verify(userRepository).save(any());
		verify(redisTemplate).delete(request.email());
	}

	@Test
	@DisplayName("이메일 인증이 되지 않은 상태로 소셜 회원가입 시 예외를 발생시킨다")
	void oauthRegister_ThrowsException_WhenEmailNotVerified() {
		// given
		OAuthRegisterRequest request = new OAuthRegisterRequest(
			"김돌돌",
			"01012341234",
			"test@example.com",
			"kakao123456",
			"KAKAO"
		);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(request.email())).thenReturn(null);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.oauthRegister(request));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.UNVERIFIED_EMAIL);
	}

	@Test
	@DisplayName("이메일 인증 값이 올바르지 않으면 소셜 회원가입 시 예외를 발생시킨다")
	void oauthRegister_ThrowsException_WhenEmailVerificationWrong() {
		// given
		OAuthRegisterRequest request = new OAuthRegisterRequest(
			"김돌돌",
			"01012341234",
			"test@example.com",
			"kakao123456",
			"KAKAO"
		);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(request.email())).thenReturn("wrong_verification");

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.oauthRegister(request));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.UNVERIFIED_EMAIL);
	}
}