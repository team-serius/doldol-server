package doldol_server.doldol.auth.service;

import static doldol_server.doldol.common.constants.CookieConstant.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.auth.dto.OAuth2ResponseStrategy;
import doldol_server.doldol.auth.dto.request.OAuthRegisterRequest;
import doldol_server.doldol.auth.dto.request.RegisterRequest;
import doldol_server.doldol.auth.dto.request.UserInfoIdCheckRequest;
import doldol_server.doldol.auth.dto.response.UserLoginIdResponse;
import doldol_server.doldol.auth.jwt.TokenProvider;
import doldol_server.doldol.auth.jwt.dto.UserTokenResponse;
import doldol_server.doldol.auth.util.CookieUtil;
import doldol_server.doldol.auth.util.GeneratorRandomUtil;
import doldol_server.doldol.common.constants.TokenConstant;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.AuthErrorCode;
import doldol_server.doldol.user.entity.SocialType;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import doldol_server.doldol.user.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

	private static final String EMAIL_VERIFIED_KEY = "verified";

	private final UserService userService;
	private final EmailService emailService;
	private final UserRepository userRepository;
	private final RedisTemplate<String, String> redisTemplate;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final OAuthSeperator oAuthSeperator;

	public void checkIdDuplicate(String id) {
		boolean isIdExists = userRepository.existsByLoginId(id);

		if (isIdExists) {
			throw new CustomException(AuthErrorCode.ID_DUPLICATED);
		}
	}

	public void checkEmailDuplicate(String email) {
		boolean isEmailExists = userRepository.existsByEmail(email);

		if (isEmailExists) {
			throw new CustomException(AuthErrorCode.EMAIl_DUPLICATED);
		}
	}

	public void checkPhoneDuplicate(String phone) {
		boolean isPhoneExists = userRepository.existsByPhone(phone);

		if (isPhoneExists) {
			throw new CustomException(AuthErrorCode.PHONE_DUPLICATED);
		}
	}

	@Transactional
	public void sendVerificationCode(String email) {
		String verificationCode = GeneratorRandomUtil.generateRandomNum();

		redisTemplate.opsForValue().set(email, verificationCode, 5, TimeUnit.MINUTES);

		emailService.sendEmailVerificationCode(email, verificationCode);
	}

	@Transactional
	public void validateVerificationCode(String email, String code) {
		Object result = redisTemplate.opsForValue().get(email);

		if (result == null) {
			throw new CustomException(AuthErrorCode.EMAIL_NOT_FOUND);
		}

		String verificationCode = result.toString();

		if (!verificationCode.equals(code)) {
			throw new CustomException(AuthErrorCode.VERIFICATION_CODE_WRONG);
		}

		redisTemplate.opsForValue().set(email, EMAIL_VERIFIED_KEY, 30, TimeUnit.MINUTES);
	}

	@Transactional
	public void register(RegisterRequest registerRequest) {
		validateAndDeleteEmailVerification(registerRequest.email());

		User user = User.builder()
			.loginId(registerRequest.id())
			.password(passwordEncoder.encode(registerRequest.password()))
			.phone(registerRequest.phone())
			.email(registerRequest.email())
			.name(registerRequest.name())
			.build();

		userRepository.save(user);
	}

	@Transactional
	public void oauthRegister(@Valid OAuthRegisterRequest oAuthRegisterRequest) {
		validateAndDeleteEmailVerification(oAuthRegisterRequest.email());

		User user = User.builder()
			.phone(oAuthRegisterRequest.phone())
			.email(oAuthRegisterRequest.email())
			.name(oAuthRegisterRequest.name())
			.socialId(oAuthRegisterRequest.socialId())
			.socialType(SocialType.getSocialType(oAuthRegisterRequest.socialType()))
			.build();

		userRepository.save(user);
	}

	@Transactional
	public void reissue(String refreshToken, HttpServletResponse response) {

		if (!tokenProvider.validateToken(refreshToken)) {
			throw new CustomException(AuthErrorCode.INVALID_TOKEN);
		}

		Claims claims = tokenProvider.getClaimsFromToken(refreshToken);
		String userId = claims.getSubject();

		String storedRefreshToken = redisTemplate.opsForValue().get(userId);
		if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
			throw new CustomException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
		}

		userService.getById(Long.parseLong(userId));

		UserTokenResponse newTokens = tokenProvider.createLoginToken(userId);

		setAccessTokenToHeader(response, newTokens.accessToken());
		setRefreshTokenToCookie(response, newTokens.refreshToken());
	}

	@Transactional
	public void withdraw(Long userId, HttpServletResponse response) {
		User user = userService.getById(userId);

		if (user.isDeleted()) {
			throw new CustomException(AuthErrorCode.ALREADY_WITHDRAWN);
		}

		if (user.getSocialId() != null) {
			OAuth2ResponseStrategy strategy = oAuthSeperator.getStrategy(user.getSocialType().name());
			strategy.unlink(user.getSocialId());
		}

		user.updateDeleteStatus();

		invalidateUserTokens(userId, response);
	}

	public void validateUserInfo(UserInfoIdCheckRequest userInfoIdCheckRequest) {
		boolean existsByEmailAndPhone = userRepository.existsByEmailAndPhone(userInfoIdCheckRequest.email(),
			userInfoIdCheckRequest.phone());

		if (!existsByEmailAndPhone) {
			throw new CustomException(AuthErrorCode.INCORRECT_EMAIL_OR_PHONE);
		}
	}

	public UserLoginIdResponse getLoginId(String email) {
		validateAndDeleteEmailVerification(email);

		User user = userRepository.findByEmail(email);

		if (user.getPassword() == null) {
			throw new CustomException(AuthErrorCode.OAUTH_LOGIN_USER, user.getSocialType().getDisplayName());
		}

		return UserLoginIdResponse.builder()
			.id(user.getLoginId())
			.build();
	}

	@Transactional
	protected void validateAndDeleteEmailVerification(String email) {
		String isVerified = Optional.ofNullable(redisTemplate.opsForValue().get(email))
			.map(Object::toString)
			.orElseThrow(() -> new CustomException(AuthErrorCode.UNVERIFIED_EMAIL));

		if (!isVerified.equals(EMAIL_VERIFIED_KEY)) {
			throw new CustomException(AuthErrorCode.UNVERIFIED_EMAIL);
		}

		redisTemplate.delete(email);
	}

	private void setAccessTokenToHeader(HttpServletResponse response, String accessToken) {
		response.setHeader(HttpHeaders.AUTHORIZATION, TokenConstant.BEARER_FIX + accessToken);
	}

	private void setRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
		ResponseCookie cookie = CookieUtil.createCookie(
			REFRESH_TOKEN_COOKIE_NAME,
			refreshToken,
			TokenConstant.REFRESH_TOKEN_EXPIRATION_DAYS * TokenConstant.DAYS_IN_MILLISECONDS
		);
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

	private void invalidateUserTokens(Long userId, HttpServletResponse response) {
		tokenProvider.deleteRefreshToken(String.valueOf(userId));

		ResponseCookie accessTokenCookie = CookieUtil.createCookie(ACCESS_TOKEN_COOKIE_NAME, null,
			TOKEN_EXPIRATION_DELETE);
		ResponseCookie refreshTokenCookie = CookieUtil.createCookie(REFRESH_TOKEN_COOKIE_NAME, null,
			TOKEN_EXPIRATION_DELETE);

		response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
	}

}
