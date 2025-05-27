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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import doldol_server.doldol.auth.dto.request.TempJoinRequest;
import doldol_server.doldol.auth.dto.response.TempSignupResponse;
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
	@DisplayName("임시 회원가입이 성공적으로 처리된다")
	void tempJoin_Success() {
		// given
		TempJoinRequest request = new TempJoinRequest(
			"test",
			"test",
			"test",
			"test",
			"01012341234",
			"test@example.com",
			true,
			true,
			true
		);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		doNothing().when(valueOperations).set(eq(request.email()), any(TempSignupResponse.class), anyLong(), any());

		// when
		when(userRepository.existsByEmail(request.email())).thenReturn(false);
		when(userRepository.existsByPhoneNumber(request.phone())).thenReturn(false);

		// then
		assertDoesNotThrow(() -> authService.tempJoin(request));

		verify(valueOperations).set(eq(request.email()), any(TempSignupResponse.class), anyLong(), any());
	}

	@Test
	@DisplayName("이메일이 중복되면 임시 회원가입 시 예외를 발생시킨다")
	void tempJoin_ThrowsException_WhenEmailDuplicated() {
		// given
		TempJoinRequest request = new TempJoinRequest(
			"test",
			"test",
			"test",
			"test",
			"01012341234",
			"test@example.com",
			true,
			true,
			true
		);

		when(userRepository.existsByEmail(request.email())).thenReturn(true);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.tempJoin(request));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.EMAIl_DUPLICATED);
	}

	@Test
	@DisplayName("전화번호가 중복되면 임시 회원가입 시 예외를 발생시킨다")
	void tempJoin_ThrowsException_WhenPhoneNumberDuplicated() {
		// given
		TempJoinRequest request = new TempJoinRequest(
			"test",
			"test",
			"test",
			"test",
			"01012341234",
			"test@example.com",
			true,
			true,
			true
		);

		when(userRepository.existsByEmail(request.email())).thenReturn(false);
		when(userRepository.existsByPhoneNumber(request.phone())).thenReturn(true);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.tempJoin(request));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.PHONE_NUMBER_DUPLICATED);
	}

	@Test
	@DisplayName("인증 코드 전송 시 임시 가입 정보가 존재하지 않으면 예외를 발생시킨다")
	void sendVerificationCode_ThrowsException_WhenEmailNotFound() {
		// given
		String email = "notfound@example.com";

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(email)).thenReturn(null);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.sendVerificationCode(email));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.EMAIL_NOT_FOUND);
	}

	@Test
	@DisplayName("인증 코드 전송이 성공적으로 처리된다")
	void sendVerificationCode_Success() {
		// given
		String email = "test@example.com";
		TempSignupResponse tempSignupResponse = TempSignupResponse.getTempSignupDate(
			new TempJoinRequest("testId", "password", "name", "nickname", "01012341234", email, true, true, true)
		);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(email)).thenReturn(tempSignupResponse);
		doNothing().when(valueOperations).set(eq(email), any(TempSignupResponse.class), anyLong(), any());
		doNothing().when(emailService).sendEmailVerificationCode(eq(email), anyString());

		// when & then
		assertDoesNotThrow(() -> authService.sendVerificationCode(email));

		verify(emailService).sendEmailVerificationCode(eq(email), anyString());
		verify(valueOperations).set(eq(email), any(TempSignupResponse.class), anyLong(), any());
	}

	@Test
	@DisplayName("인증 코드 검증이 성공적으로 처리된다")
	void validateVerificationCode_Success() {
		// given
		String email = "test@example.com";
		String verificationCode = "123456";

		TempSignupResponse tempSignupResponse = TempSignupResponse.getTempSignupDate(
			new TempJoinRequest("testId", "password", "name", "nickname", "01012341234", email, true, true, true)
		);
		tempSignupResponse.initVerificationCode(verificationCode);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(email)).thenReturn(tempSignupResponse);
		doNothing().when(valueOperations).set(eq(email), any(TempSignupResponse.class), anyLong(), any());

		// when & then
		assertDoesNotThrow(() -> authService.validateVerificationCode(email, verificationCode));

		verify(valueOperations).set(eq(email), any(TempSignupResponse.class), anyLong(), any());
	}

	@Test
	@DisplayName("잘못된 인증 코드로 검증 시 예외를 발생시킨다")
	void validateVerificationCode_ThrowsException_WhenCodeWrong() {
		// given
		String email = "test@example.com";
		String correctCode = "123456";
		String wrongCode = "654321";

		TempSignupResponse tempSignupResponse = TempSignupResponse.getTempSignupDate(
			new TempJoinRequest("testId", "password", "name", "nickname", "01012341234", email, true, true, true)
		);
		tempSignupResponse.initVerificationCode(correctCode);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(email)).thenReturn(tempSignupResponse);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.validateVerificationCode(email, wrongCode));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.VERIFICATION_CODE_WRONG);
	}

	@Test
	@DisplayName("본 가입이 성공적으로 처리된다")
	void join_Success() {
		// given
		String email = "test@example.com";

		TempSignupResponse tempSignupResponse = TempSignupResponse.getTempSignupDate(
			new TempJoinRequest("testId", "password", "name", "nickname", "01012341234", email, true, true, true)
		);
		tempSignupResponse.initVerificationCode("123456");
		tempSignupResponse.updateVerificationStatus();

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(email)).thenReturn(tempSignupResponse);
		when(redisTemplate.delete(email)).thenReturn(true);
		when(userRepository.save(any())).thenReturn(null);

		// when & then
		assertDoesNotThrow(() -> authService.join(email));

		verify(userRepository).save(any());
		verify(redisTemplate).delete(email);
	}

	@Test
	@DisplayName("이메일 인증이 되지 않은 상태로 본 가입 시 예외를 발생시킨다")
	void join_ThrowsException_WhenEmailNotVerified() {
		// given
		String email = "test@example.com";

		TempSignupResponse tempSignupResponse = TempSignupResponse.getTempSignupDate(
			new TempJoinRequest("testId", "password", "name", "nickname", "01012341234", email, true, true, true)
		);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(email)).thenReturn(tempSignupResponse);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.join(email));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.UNVERIFIED_EMAIL);
	}
}