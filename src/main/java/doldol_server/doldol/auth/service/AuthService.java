package doldol_server.doldol.auth.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.auth.dto.OAuth2ResponseStrategy;
import doldol_server.doldol.auth.dto.request.OAuthRegisterRequest;
import doldol_server.doldol.auth.dto.request.RegisterRequest;
import doldol_server.doldol.auth.dto.request.UserInfoIdCheckRequest;
import doldol_server.doldol.auth.dto.response.ReissueTokenResponse;
import doldol_server.doldol.auth.dto.response.UserLoginIdResponse;
import doldol_server.doldol.auth.jwt.TokenProvider;
import doldol_server.doldol.auth.jwt.dto.UserTokenResponse;
import doldol_server.doldol.auth.util.GeneratorRandomUtil;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.AuthErrorCode;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import doldol_server.doldol.user.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

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
	public void oauthRegister(OAuthRegisterRequest oAuthRegisterRequest) {
		validateAndDeleteEmailVerification(oAuthRegisterRequest.email());

		User user = User.builder()
			.phone(oAuthRegisterRequest.phone())
			.email(oAuthRegisterRequest.email())
			.name(oAuthRegisterRequest.name())
			.socialId(oAuthRegisterRequest.socialId())
			.socialType(oAuthRegisterRequest.socialType())
			.build();

		userRepository.save(user);
	}

	@Transactional
	public ReissueTokenResponse reissue(String refreshToken) {

		if (!tokenProvider.validateToken(refreshToken)) {
			throw new CustomException(AuthErrorCode.INVALID_TOKEN);
		}

		Claims claims = tokenProvider.getClaimsFromToken(refreshToken);
		String userId = claims.getSubject();

		String storedRefreshToken = redisTemplate.opsForValue().get(userId);
		if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
			throw new CustomException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
		}

		User user = userService.getById(Long.parseLong(userId));

		UserTokenResponse newTokens = tokenProvider.createLoginToken(userId, user.getRole().getRole());

		return ReissueTokenResponse
			.builder()
			.accessToken(newTokens.accessToken())
			.refreshToken(newTokens.refreshToken())
			.build();
	}

	@Transactional
	public void withdraw(Long userId) {
		User user = userService.getById(userId);

		if (user.isDeleted()) {
			throw new CustomException(AuthErrorCode.ALREADY_WITHDRAWN);
		}

		if (user.getSocialId() != null) {
			OAuth2ResponseStrategy strategy = oAuthSeperator.getStrategy(user.getSocialType().name());
			strategy.unlink(user.getSocialId());
			user.deleteOAuthInfo();
		}

		user.updateDeleteStatus();

		tokenProvider.deleteRefreshToken(String.valueOf(userId));
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

		String loginId = user.getLoginId();

		String maskingedId = maskingId(loginId);
		return UserLoginIdResponse.builder()
			.id(maskingedId)
			.build();
	}

	@Transactional
	public void resetPassword(String email) {
		validateAndDeleteEmailVerification(email);

		User user = userRepository.findByEmail(email);

		if (user.getPassword() == null) {
			throw new CustomException(AuthErrorCode.OAUTH_LOGIN_USER, user.getSocialType().getDisplayName());
		}

		String tempPassword = GeneratorRandomUtil.generateRandomString();

		user.updateUserPassword(passwordEncoder.encode(tempPassword));

		emailService.sendEmailTempPassword(email, tempPassword);
	}

	public void checkRegisterInfoDuplicate(String email, String phone) {
		boolean existsByEmail = userRepository.existsByEmail(email);
		boolean existsByPhone = userRepository.existsByPhone(phone);

		if (existsByEmail && !existsByPhone) {
			throw new CustomException(AuthErrorCode.EMAIl_DUPLICATED);
		} else if (!existsByEmail && existsByPhone) {
			throw new CustomException(AuthErrorCode.PHONE_DUPLICATED);
		} else if (existsByEmail && existsByPhone) {
			throw new CustomException(AuthErrorCode.EMAIL_PHONE_DUPLICATED);
		}
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

	private String maskingId(String loginId) {
		String prefix = loginId.substring(0, 4);
		String maskedPart = "*".repeat(loginId.length() - 4);

		return prefix + maskedPart;
	}

}
