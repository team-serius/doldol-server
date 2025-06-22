package doldol_server.doldol.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import doldol_server.doldol.auth.dto.OAuth2ResponseStrategy;
import doldol_server.doldol.auth.dto.request.OAuthRegisterRequest;
import doldol_server.doldol.auth.dto.request.RegisterRequest;
import doldol_server.doldol.auth.dto.request.UserInfoIdCheckRequest;
import doldol_server.doldol.auth.dto.response.UserLoginIdResponse;
import doldol_server.doldol.auth.jwt.TokenProvider;
import doldol_server.doldol.auth.jwt.dto.UserTokenResponse;
import doldol_server.doldol.common.ServiceTest;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.AuthErrorCode;
import doldol_server.doldol.common.exception.errorCode.UserErrorCode;
import doldol_server.doldol.user.entity.Role;
import doldol_server.doldol.user.entity.SocialType;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import io.jsonwebtoken.Claims;

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
	private RedisTemplate<String, String> redisTemplate;

	@MockitoBean
	private ValueOperations<String, String> valueOperations;

	@MockitoBean
	private PasswordEncoder passwordEncoder;

	@MockitoBean
	private TokenProvider tokenProvider;

	@MockitoBean
	private OAuthSeperator oAuthSeperator;

	@MockitoBean
	private OAuth2ResponseStrategy oAuth2ResponseStrategy;

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
			SocialType.KAKAO
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
			SocialType.KAKAO
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
			SocialType.KAKAO
		);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(request.email())).thenReturn("wrong_verification");

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.oauthRegister(request));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.UNVERIFIED_EMAIL);
	}

	@Test
	@DisplayName("유효하지 않은 리프레시 토큰으로 재발급 시 예외를 발생시킨다")
	void reissue_ThrowsException_WhenInvalidToken() {
		// given
		String refreshToken = "invalidRefreshToken";

		when(tokenProvider.validateToken(refreshToken)).thenReturn(false);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.reissue(refreshToken));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.INVALID_TOKEN);
		verify(tokenProvider).validateToken(refreshToken);
		verify(redisTemplate, never()).opsForValue();
	}

	@Test
	@DisplayName("저장된 리프레시 토큰이 없을 때 재발급 시 예외를 발생시킨다")
	void reissue_ThrowsException_WhenStoredTokenNotFound() {
		// given
		String refreshToken = "validRefreshToken";
		String userId = "1";

		Claims claims = mock(Claims.class);

		when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
		when(tokenProvider.getClaimsFromToken(refreshToken)).thenReturn(claims);
		when(claims.getSubject()).thenReturn(userId);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(userId)).thenReturn(null);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.reissue(refreshToken));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
	}

	@Test
	@DisplayName("저장된 리프레시 토큰과 요청된 토큰이 다를 때 재발급 시 예외를 발생시킨다")
	void reissue_ThrowsException_WhenTokenMismatch() {
		// given
		String refreshToken = "validRefreshToken";
		String userId = "1";
		String storedRefreshToken = "differentRefreshToken";

		Claims claims = mock(Claims.class);

		when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
		when(tokenProvider.getClaimsFromToken(refreshToken)).thenReturn(claims);
		when(claims.getSubject()).thenReturn(userId);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(userId)).thenReturn(storedRefreshToken);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.reissue(refreshToken));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
	}

	@Test
	@DisplayName("존재하지 않는 사용자로 토큰 재발급 시 예외를 발생시킨다")
	void reissue_ThrowsException_WhenUserNotFound() {
		// given
		String refreshToken = "validRefreshToken";
		String userId = "999";

		Claims claims = mock(Claims.class);

		when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
		when(tokenProvider.getClaimsFromToken(refreshToken)).thenReturn(claims);
		when(claims.getSubject()).thenReturn(userId);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(userId)).thenReturn(refreshToken);
		when(userRepository.findById(Long.parseLong(userId))).thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.reissue(refreshToken));

		assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
		verify(userRepository).findById(Long.parseLong(userId));
	}

	@Test
	@DisplayName("토큰 재발급이 성공적으로 처리된다")
	void reissue_Success() {
		// given
		String refreshToken = "validRefreshToken";
		String userId = "1";

		User user = User.builder()
			.loginId("testuser")
			.email("test@example.com")
			.build();

		Claims claims = mock(Claims.class);
		UserTokenResponse newTokens = new UserTokenResponse("newAccessToken", "newRefreshToken");

		when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
		when(tokenProvider.getClaimsFromToken(refreshToken)).thenReturn(claims);
		when(claims.getSubject()).thenReturn(userId);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(userId)).thenReturn(refreshToken);
		when(userRepository.findById(Long.parseLong(userId))).thenReturn(Optional.of(user));
		when(tokenProvider.createLoginToken(userId, Role.USER.getRole())).thenReturn(newTokens);

		// when & then
		assertDoesNotThrow(() -> authService.reissue(refreshToken));

		verify(tokenProvider).validateToken(refreshToken);
		verify(tokenProvider).getClaimsFromToken(refreshToken);
		verify(valueOperations).get(userId);
		verify(userRepository).findById(Long.parseLong(userId));
		verify(tokenProvider).createLoginToken(userId, Role.USER.getRole());
	}

	@Test
	@DisplayName("존재하지 않는 사용자로 탈퇴 시 예외를 발생시킨다")
	void withdraw_ThrowsException_WhenUserNotFound() {
		// given
		Long userId = 999L;

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.withdraw(userId));

		assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
		verify(userRepository).findById(userId);
		verify(oAuthSeperator, never()).getStrategy(anyString());
		verify(tokenProvider, never()).deleteRefreshToken(anyString());
	}

	@Test
	@DisplayName("이미 탈퇴한 사용자로 탈퇴 시 예외를 발생시킨다")
	void withdraw_ThrowsException_WhenAlreadyWithdrawn() {
		// given
		Long userId = 1L;

		User deletedUser = User.builder()
			.loginId("deleteduser")
			.email("deleted@example.com")
			.build();
		deletedUser.updateDeleteStatus();

		when(userRepository.findById(userId)).thenReturn(Optional.of(deletedUser));

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.withdraw(userId));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.ALREADY_WITHDRAWN);
		verify(userRepository).findById(userId);
		verify(oAuthSeperator, never()).getStrategy(anyString());
		verify(tokenProvider, never()).deleteRefreshToken(anyString());
	}

	@Test
	@DisplayName("일반 사용자 탈퇴가 성공적으로 처리된다")
	void withdraw_Success_RegularUser() {
		// given
		Long userId = 1L;

		User user = User.builder()
			.loginId("testuser")
			.email("test@example.com")
			.password("encoded_password")
			.build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		doNothing().when(tokenProvider).deleteRefreshToken(anyString());

		// when & then
		assertDoesNotThrow(() -> authService.withdraw(userId));

		assertThat(user.isDeleted()).isTrue();
		verify(userRepository).findById(userId);
		verify(oAuthSeperator, never()).getStrategy(anyString());
		verify(tokenProvider).deleteRefreshToken(String.valueOf(userId));
	}

	@Test
	@DisplayName("소셜 사용자 탈퇴가 성공적으로 처리된다")
	void withdraw_Success_SocialUser() {
		// given
		Long userId = 1L;
		String socialId = "kakao123456";

		User socialUser = User.builder()
			.email("social@example.com")
			.socialId(socialId)
			.socialType(SocialType.KAKAO)
			.build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(socialUser));
		when(oAuthSeperator.getStrategy(SocialType.KAKAO.name())).thenReturn(oAuth2ResponseStrategy);
		doNothing().when(oAuth2ResponseStrategy).unlink(socialId);
		doNothing().when(tokenProvider).deleteRefreshToken(anyString());

		// when & then
		assertDoesNotThrow(() -> authService.withdraw(userId));

		assertThat(socialUser.isDeleted()).isTrue();
		verify(userRepository).findById(userId);
		verify(oAuthSeperator).getStrategy(SocialType.KAKAO.name());
		verify(oAuth2ResponseStrategy).unlink(socialId);
		verify(tokenProvider).deleteRefreshToken(String.valueOf(userId));
	}

	@Test
	@DisplayName("아이디 찾기가 성공적으로 처리된다")
	void getLoginId_Success() {
		// given
		String email = "test@example.com";
		String loginId = "testuser123";

		User user = User.builder()
			.loginId(loginId)
			.email(email)
			.password("encodedPassword")
			.build();

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(email)).thenReturn(EMAIL_VERIFIED_KEY);
		when(redisTemplate.delete(email)).thenReturn(true);
		when(userRepository.findByEmail(email)).thenReturn(user);

		// when
		UserLoginIdResponse result = authService.getLoginId(email);

		// then
		assertThat(result.id()).isEqualTo("test*******");
		verify(valueOperations).get(email);
		verify(redisTemplate).delete(email);
		verify(userRepository).findByEmail(email);
	}

	@Test
	@DisplayName("이메일 인증이 되지 않은 상태로 아이디 찾기 시 예외를 발생시킨다")
	void getLoginId_ThrowsException_WhenEmailNotVerified() {
		// given
		String email = "test@example.com";

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(email)).thenReturn(null);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.getLoginId(email));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.UNVERIFIED_EMAIL);
		verify(userRepository, never()).findByEmail(anyString());
	}

	@Test
	@DisplayName("소셜 로그인 사용자로 아이디 찾기 시 예외를 발생시킨다")
	void getLoginId_ThrowsException_WhenOAuthUser() {
		// given
		String email = "test@example.com";

		User oauthUser = User.builder()
			.email(email)
			.socialId("kakao123456")
			.socialType(SocialType.KAKAO)
			.build();

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(email)).thenReturn(EMAIL_VERIFIED_KEY);
		when(redisTemplate.delete(email)).thenReturn(true);
		when(userRepository.findByEmail(email)).thenReturn(oauthUser);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.getLoginId(email));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.OAUTH_LOGIN_USER);
		verify(userRepository).findByEmail(email);
	}

	@Test
	@DisplayName("비밀번호 초기화가 성공적으로 처리된다")
	void resetPassword_Success() {
		// given
		String email = "test@example.com";
		String tempPassword = "tempPass123";

		User user = User.builder()
			.loginId("testuser")
			.email(email)
			.password("oldEncodedPassword")
			.build();

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(email)).thenReturn(EMAIL_VERIFIED_KEY);
		when(redisTemplate.delete(email)).thenReturn(true);
		when(userRepository.findByEmail(email)).thenReturn(user);
		when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
		doNothing().when(emailService).sendEmailTempPassword(eq(email), anyString());

		// when & then
		assertDoesNotThrow(() -> authService.resetPassword(email));

		verify(valueOperations).get(email);
		verify(redisTemplate).delete(email);
		verify(userRepository).findByEmail(email);
		verify(passwordEncoder).encode(anyString());
		verify(emailService).sendEmailTempPassword(eq(email), anyString());
	}

	@Test
	@DisplayName("이메일 인증이 되지 않은 상태로 비밀번호 초기화 시 예외를 발생시킨다")
	void resetPassword_ThrowsException_WhenEmailNotVerified() {
		// given
		String email = "test@example.com";

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(email)).thenReturn(null);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.resetPassword(email));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.UNVERIFIED_EMAIL);
		verify(userRepository, never()).findByEmail(anyString());
		verify(emailService, never()).sendEmailTempPassword(anyString(), anyString());
	}

	@Test
	@DisplayName("사용자 정보 검증이 성공적으로 처리된다")
	void validateUserInfo_Success() {
		// given
		UserInfoIdCheckRequest request = new UserInfoIdCheckRequest(
			"test",
			"test@example.com",
			"01012341234"
		);

		when(userRepository.existsByNameAndEmailAndPhone(request.name(), request.email(), request.phone())).thenReturn(
			true);

		// when & then
		assertDoesNotThrow(() -> authService.validateUserInfo(request.name(), request.email(), request.phone()));

		verify(userRepository).existsByNameAndEmailAndPhone(request.name(), request.email(), request.phone());
	}

	@Test
	@DisplayName("일치하지 않는 사용자 정보로 검증 시 예외를 발생시킨다")
	void validateUserInfo_ThrowsException_WhenInfoNotMatch() {
		// given
		UserInfoIdCheckRequest request = new UserInfoIdCheckRequest(
			"test",
			"test@example.com",
			"01012341234"
		);

		when(userRepository.existsByNameAndEmailAndPhone(request.name(),request.email(), request.phone())).thenReturn(false);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.validateUserInfo(request.name(),request.email(), request.phone()));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.INCORRECT_NAME_OR_EMAIL_OR_PHONE);
		verify(userRepository).existsByNameAndEmailAndPhone(request.name(),request.email(), request.phone());
	}

	@Test
	@DisplayName("이메일, 전화번호 모두 중복되지 않으면 정상 처리된다")
	void checkRegisterInfoDuplicate_Success_NoDuplication() {
		// given
		String email = "test@example.com";
		String phone = "01012341234";

		when(userRepository.existsByEmail(email)).thenReturn(false);
		when(userRepository.existsByPhone(phone)).thenReturn(false);

		// when & then
		assertDoesNotThrow(() -> authService.checkRegisterInfoDuplicate(email, phone));

		verify(userRepository).existsByEmail(email);
		verify(userRepository).existsByPhone(phone);
	}

	@Test
	@DisplayName("이메일만 중복되면 이메일 중복 예외를 발생시킨다")
	void checkRegisterInfoDuplicate_ThrowsException_WhenEmailDuplicated() {
		// given
		String email = "test@example.com";
		String phone = "01012341234";

		when(userRepository.existsByEmail(email)).thenReturn(true);
		when(userRepository.existsByPhone(phone)).thenReturn(false);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.checkRegisterInfoDuplicate(email, phone));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.EMAIl_DUPLICATED);
		verify(userRepository).existsByEmail(email);
		verify(userRepository).existsByPhone(phone);
	}

	@Test
	@DisplayName("전화번호만 중복되면 전화번호 중복 예외를 발생시킨다")
	void checkRegisterInfoDuplicate_ThrowsException_WhenPhoneDuplicated() {
		// given
		String email = "test@example.com";
		String phone = "01012341234";

		when(userRepository.existsByEmail(email)).thenReturn(false);
		when(userRepository.existsByPhone(phone)).thenReturn(true);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.checkRegisterInfoDuplicate(email, phone));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.PHONE_DUPLICATED);
		verify(userRepository).existsByEmail(email);
		verify(userRepository).existsByPhone(phone);
	}

	@Test
	@DisplayName("이메일과 전화번호 모두 중복되면 이메일-전화번호 중복 예외를 발생시킨다")
	void checkRegisterInfoDuplicate_ThrowsException_WhenBothDuplicated() {
		// given
		String email = "test@example.com";
		String phone = "01012341234";

		when(userRepository.existsByEmail(email)).thenReturn(true);
		when(userRepository.existsByPhone(phone)).thenReturn(true);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> authService.checkRegisterInfoDuplicate(email, phone));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.EMAIL_PHONE_DUPLICATED);
		verify(userRepository).existsByEmail(email);
		verify(userRepository).existsByPhone(phone);
	}
}
