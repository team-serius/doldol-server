package doldol_server.doldol.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import doldol_server.doldol.auth.dto.request.TempJoinRequest;
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

		// when
		when(userRepository.existsByEmail(request.email())).thenReturn(false);
		when(userRepository.existsByPhoneNumber(request.phone())).thenReturn(false);

		// then
		assertDoesNotThrow(() -> authService.tempJoin(request));
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
	@DisplayName("인증 코드 전송 시 이메일이 존재하지 않으면 예외를 발생시킨다")
	void sendVerificationCode_ThrowsException_WhenEmailNotFound() {
		// given
		String email = "notfound@example.com";

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.sendVerificationCode(email));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.EMAIL_NOT_FOUND);
	}

}