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
import doldol_server.doldol.auth.dto.response.ReissueTokenResponse;
import doldol_server.doldol.auth.dto.response.UserLoginIdResponse;
import doldol_server.doldol.auth.jwt.TokenProvider;
import doldol_server.doldol.auth.jwt.dto.UserTokenResponse;
import doldol_server.doldol.auth.util.GeneratorRandomUtil;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.AuthErrorCode;
import doldol_server.doldol.common.exception.errorCode.UserErrorCode;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import doldol_server.doldol.user.service.UserService;
import io.jsonwebtoken.Claims;
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
			log.warn("아이디 중복 확인: 이미 존재하는 아이디={}", id);
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
			log.warn("인증 코드 만료: email={}", email);
			throw new CustomException(AuthErrorCode.EMAIL_NOT_FOUND);
		}

		String verificationCode = result.toString();

		if (!verificationCode.equals(code)) {
			log.warn("인증 코드 불일치: email={}, 입력코드={}", email, code);
			throw new CustomException(AuthErrorCode.VERIFICATION_CODE_WRONG);
		}

		log.info("이메일 인증 완료: email={}", email);
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

		log.info("자체 회원가입 완료: userId={}, email={}, name='{}'",
			user.getId(), user.getEmail(), user.getName());
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

		log.info("소셜 회원가입 완료: userId={}, email={}, socialType={}",
			user.getId(), user.getEmail(), user.getSocialType());
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

		log.info("토큰 재발급 완료: userId={}", userId);

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
			try {
				OAuth2ResponseStrategy strategy = oAuthSeperator.getStrategy(user.getSocialType().name());
				strategy.unlink(user.getSocialId());
				user.deleteOAuthInfo();
			} catch (Exception e) {
				log.error("소셜 계정 연동 해제 실패: userId={}, socialType={}, 오류={}",
					userId, user.getSocialType(), e.getMessage(), e);
			}
		}

		user.updateDeleteStatus();

		tokenProvider.deleteRefreshToken(String.valueOf(userId));

		log.info("소셜 계정 연동 해제: userId={}, socialType={}",
			userId, user.getSocialType());
	}

	public void validateUserInfo(String name, String email, String phone) {
		boolean existsByNameAndEmailAndPhone = userRepository.existsByNameAndEmailAndPhone(name, email, phone);

		if (!existsByNameAndEmailAndPhone) {
			throw new CustomException(AuthErrorCode.INCORRECT_NAME_OR_EMAIL_OR_PHONE);
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

		log.info("비밀번호 재설정 완료: email={}, userId={}", email, user.getId());
	}

	public void checkRegisterInfoDuplicate(String email, String phone) {
		boolean existsByEmail = userRepository.existsByEmail(email);
		boolean existsByPhone = userRepository.existsByPhone(phone);

		if (existsByEmail && !existsByPhone) {
			log.warn("이메일 중복: email={}", email);
			throw new CustomException(AuthErrorCode.EMAIl_DUPLICATED);
		} else if (!existsByEmail && existsByPhone) {
			log.warn("전화번호 중복: phone={}", phone);
			throw new CustomException(AuthErrorCode.PHONE_DUPLICATED);
		} else if (existsByEmail && existsByPhone) {
			log.warn("이메일/전화번호 모두 중복: email={}, phone={}", email, phone);
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

	public void checkEmailExists(String email) {
		boolean existsByEmail = userRepository.existsByEmail(email);

		if (!existsByEmail) {
			throw new CustomException(UserErrorCode.EMAIL_NOT_FOUND);
		}
	}

	public void validatePasswordInfo(String id, String email) {
		boolean existsByLoginIdAndEmail = userRepository.existsByLoginIdAndEmail(id, email);

		if (!existsByLoginIdAndEmail) {
			throw new CustomException(UserErrorCode.ID_OR_EMAIL_NOT_FOUND);
		}
	}
}
